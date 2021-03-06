package sch.xmut.wu.apicourt.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import sch.xmut.wu.apicourt.constant.CacheConstant;
import sch.xmut.wu.apicourt.entity.ApiOssEntity;
import sch.xmut.wu.apicourt.entity.ArenaEntity;
import sch.xmut.wu.apicourt.entity.CourtEntity;
import sch.xmut.wu.apicourt.entity.ArenaCommentEntity;
import sch.xmut.wu.apicourt.entity.UserCollectEntity;
import sch.xmut.wu.apicourt.entity.UserEntity;
import sch.xmut.wu.apicourt.http.request.ArenaRequest;
import sch.xmut.wu.apicourt.http.response.ArenaResponse;
import sch.xmut.wu.apicourt.http.response.BaseResponse;
import sch.xmut.wu.apicourt.http.response.LayerResponse;
import sch.xmut.wu.apicourt.http.response.OssImageResponse;
import sch.xmut.wu.apicourt.http.vo.ApiOss;
import sch.xmut.wu.apicourt.http.vo.Arena;
import sch.xmut.wu.apicourt.http.vo.ArenaComment;
import sch.xmut.wu.apicourt.http.vo.Court;
import sch.xmut.wu.apicourt.http.vo.User;
import sch.xmut.wu.apicourt.repository.ApiOssRepository;
import sch.xmut.wu.apicourt.repository.ArenaRepository;
import sch.xmut.wu.apicourt.repository.CourtRepository;
import sch.xmut.wu.apicourt.repository.ArenaCommentRepository;
import sch.xmut.wu.apicourt.repository.UserCollectRepository;
import sch.xmut.wu.apicourt.repository.UserRepository;
import sch.xmut.wu.apicourt.utils.SystemUtils;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by wu on 2020/04/13
 */
@Service
public class ArenaService {
    public static final String BUCKET_NAME = "image-steganography";
    public static final String IMAGE_FOLDER = "image/";
    public static final String RESULT_IMAGE_FOLDER = "resultImage/";
    public static final String RESULT_IMAGE_STYLE2 = "x-oss-process=style/resultImage_style2";
    @Autowired
    private ArenaRepository arenaRepository;
    @Autowired
    private CourtRepository courtRepository;
    @Autowired
    private ApiOssRepository apiOssRepository;
    @Autowired
    private ArenaCommentRepository arenaCommentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCollectRepository userCollectRepository;

    public ArenaResponse list(ArenaRequest request) {
        ArenaResponse response = new ArenaResponse();
        List<ArenaEntity> arenaEntityList;
        List<Arena> arenaList = new ArrayList<>();
        if (request.getListType() == 0) {
            arenaEntityList = arenaRepository.findAllByIsRecommend(ArenaEntity.RECOMMEND_YES);
        } else {
            arenaEntityList = arenaRepository.findAll();
        }
        buildVoByArenaEntity(arenaEntityList, arenaList, request);
        if (request.getListType() == 0) {
            response.setArenaList(arenaList);
            return response;
        } else if (request.getListType() == 1) {
            Collections.sort(arenaList, new Comparator<Arena>() {
                @Override
                public int compare(Arena o1, Arena o2) {
                    double diff = o1.getTotalScore() - o2.getTotalScore();
                    if (diff > 0.0) {
                        return -1;
                    } else if (diff < 0.0) {
                        return 1;
                    }
                    return 0;
                }
            });
        } else if (request.getListType() == 2) {
            Collections.sort(arenaList, new Comparator<Arena>() {
                @Override
                public int compare(Arena o1, Arena o2) {
                    double diff = o1.getDistance() - o2.getDistance();
                    if (diff > 0.0) {
                        return 1;
                    } else if (diff < 0.0) {
                        return -1;
                    }
                    return 0;
                }
            });
        } else {
            Collections.sort(arenaList, new Comparator<Arena>() {
                @Override
                public int compare(Arena o1, Arena o2) {
                    double diff = o1.getPrice() - o2.getPrice();
                    if (diff > 0.0) {
                        return 1;
                    } else if (diff < 0.0) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        response.setArenaList(arenaList);
        return response;
    }

    public void buildVoByArenaEntity(List<ArenaEntity> arenaEntityList, List<Arena> arenaList, ArenaRequest request) {
        for (ArenaEntity arenaEntity : arenaEntityList) {
            Arena arena = new Arena();
            BeanUtils.copyProperties(arenaEntity, arena);
            buildCommon(arena);
            //计算球馆距离
            Point2D point2D = new Point2D.Double(request.getJingdu(), request.getWeidu());
            Point2D point2D1 = new Point2D.Double(arenaEntity.getJingdu(), arenaEntity.getWeidu());
            arena.setDistance(SystemUtils.getDistance(point2D, point2D1));
            arena.setDistanceStr("约" + SystemUtils.getDistance(point2D, point2D1) + "km");
            arenaList.add(arena);
        }
    }

    public void buildCommon(Arena arena) {
        List<CourtEntity> courtEntityList = courtRepository.findAllByArenaId(arena.getId());//查询该球馆的所有球场
        if (!CollectionUtils.isEmpty(courtEntityList)) {
            Double countTemp1 = 0.0;
            Double countTemp2 = 0.0;
            Double countTemp3 = 0.0;
            for (CourtEntity courtEntity : courtEntityList) {
                //球馆评分计算方式
                countTemp1 = countTemp1 + courtEntity.getScore();
                //球馆综合分计算方式
                countTemp2 = countTemp2 + (courtEntity.getRentWork() + courtEntity.getRentWeekend())/2 - courtEntity.getScore()*10;
                //球馆均价计算方式
                countTemp3 = countTemp3 + (courtEntity.getRentWork() + courtEntity.getRentWeekend())/2;
            }
            arena.setSingleScore(Double.valueOf(String.format("%.2f", countTemp1/courtEntityList.size())));
            arena.setTotalScore(Double.valueOf(String.format("%.2f", countTemp2/courtEntityList.size())));
            arena.setPrice((int) (countTemp3/courtEntityList.size()));
        }
    }

    public ArenaResponse detail(ArenaRequest request) {
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        ArenaResponse response = new ArenaResponse();
        Optional<ArenaEntity> arenaEntityOptional = arenaRepository.findById(request.getArenaId());
        if (arenaEntityOptional.isPresent()) {
            Arena arena = new Arena();
            BeanUtils.copyProperties(arenaEntityOptional.get(), arena);
            buildCommon(arena);
            List<UserCollectEntity> userCollectEntityList = userCollectRepository.findAllByUserId(user.getId());
            for (UserCollectEntity userCollectEntity : userCollectEntityList) {
                if (arena.getId() == userCollectEntity.getArenaId()) {
                    response.setCollectStatus(true);
                    break;
                }
            }
            //计算球馆距离
            Point2D point2D = new Point2D.Double(request.getJingdu(), request.getWeidu());
            Point2D point2D1 = new Point2D.Double(arenaEntityOptional.get().getJingdu(), arenaEntityOptional.get().getWeidu());
            arena.setDistanceStr("约" + SystemUtils.getDistance(point2D, point2D1) + "km");
            response.setArena(arena);
        }
        List<CourtEntity> courtEntityList = courtRepository.findAllByArenaId(request.getArenaId());
        List<Court> courtList = new ArrayList<>();
        for (CourtEntity courtEntity : courtEntityList) {
            Court court = new Court();
            BeanUtils.copyProperties(courtEntity, court);
            courtList.add(court);
        }
        response.setCourtList(courtList);
        List<ArenaComment> arenaCommentList = new ArrayList<>();
        List<ArenaCommentEntity> arenaCommentEntityList = arenaCommentRepository.findAllByArenaIdOrderByCommentTimeDesc(request.getArenaId());
        for (ArenaCommentEntity arenaCommentEntity : arenaCommentEntityList) {
            ArenaComment arenaComment = new ArenaComment();
            arenaComment.setUserId(arenaCommentEntity.getUserId());
            Optional<UserEntity> userEntityOptional = userRepository.findById(arenaCommentEntity.getUserId());
            if (userEntityOptional.isPresent()) {
                arenaComment.setUserName(userEntityOptional.get().getUserName());
                arenaComment.setPortrait(userEntityOptional.get().getPortrait());
            }
            arenaComment.setCommentTime(SystemUtils.formatDate2(arenaCommentEntity.getCommentTime()));
            arenaComment.setCommentText(arenaCommentEntity.getCommentText());
            arenaCommentList.add(arenaComment);
        }
        response.setArenaCommentList(arenaCommentList);
        return response;
    }

    public ArenaResponse search(ArenaRequest request) {
        ArenaResponse response = new ArenaResponse();
        List<ArenaEntity> arenaEntityList = arenaRepository.findAllByNameLike(request.getArenaName());
        List<Arena> arenaList = new ArrayList<>();
        buildVoByArenaEntity(arenaEntityList, arenaList, request);
        response.setArenaList(arenaList);
        return response;
    }

    public LayerResponse getArenaList(Pageable pageable) {
        LayerResponse response = new LayerResponse();
        Page<ArenaEntity> arenaEntityPage = arenaRepository.findAll(pageable);
        List<Arena> arenaList = new ArrayList<>();
        for (ArenaEntity arenaEntity : arenaEntityPage) {
            Arena arena = new Arena();
            BeanUtils.copyProperties(arenaEntity, arena);
            if (arenaEntity.getStatus() == ArenaEntity.STATUS_REST) {
                arena.setStatusStr("休息");
            } else if (arenaEntity.getStatus() == ArenaEntity.STATUS_BUSY) {
                arena.setStatusStr("营业中");
            } else {
                arena.setStatusStr("倒闭(删除)");
            }
            if (arenaEntity.getIsRecommend() == ArenaEntity.RECOMMEND_YES) {
                arena.setRecommendStr("推荐");
            } else {
                arena.setRecommendStr("不推荐");
            }
            arenaList.add(arena);
        }
        List<ArenaEntity> list = arenaRepository.findAll();
        response.setCount(list.size());
        response.setData(arenaList);
        return response;
    }

    public BaseResponse arenaAdd(ArenaRequest request) {
        ArenaEntity arenaEntity = new ArenaEntity();
        BeanUtils.copyProperties(request, arenaEntity);
        arenaEntity.setName(request.getArenaName());
        arenaEntity.setStartTime(new Date());
        arenaEntity.setEndTime(new Date());
        arenaRepository.save(arenaEntity);
        return new BaseResponse();
    }

    public BaseResponse arenaDelete(Integer id) {
        Optional<ArenaEntity> arenaEntityOptional = arenaRepository.findById(id);
        ArenaEntity arenaEntity = arenaEntityOptional.get();
        arenaEntity.setStatus(ArenaEntity.STATUS_DELETE);
        arenaRepository.save(arenaEntity);
        return new BaseResponse();
    }

    public BaseResponse arenaOpen(Integer id) {
        Optional<ArenaEntity> arenaEntityOptional = arenaRepository.findById(id);
        ArenaEntity arenaEntity = arenaEntityOptional.get();
        arenaEntity.setStatus(ArenaEntity.STATUS_BUSY);
        arenaRepository.save(arenaEntity);
        return new BaseResponse();
    }

    //文件上传
    public OssImageResponse imageUploadOss(MultipartFile file) {
        OssImageResponse ossImageResponse = new OssImageResponse();
        ApiOss apiOss = getApiOssInfo();
        OSSClient ossClient = new OSSClient(apiOss.getEndPoint(), apiOss.getAccessKey(), apiOss.getAccessSecret());
        String resultImageUrl = null;
        String fileName = file.getOriginalFilename();
        String imageName = UUID.randomUUID().toString() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        Long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setCacheControl("no-cache");
        metadata.setHeader("Pragma", "no-cache");
        metadata.setContentEncoding("utf-8");
        metadata.setContentType(getContentType(imageName));
        metadata.setContentDisposition("filename/filesize=" + imageName + "/" + fileSize + "Byte.");
        //上传文件
        try {
            ossClient.putObject(BUCKET_NAME, IMAGE_FOLDER + imageName, file.getInputStream(), metadata);
            resultImageUrl = "http://" + BUCKET_NAME + "." + apiOss.getEndPoint() + "/" + IMAGE_FOLDER + imageName;
        } catch (IOException e) {
        }
        ossImageResponse.setIamgeUrl(resultImageUrl);
        return ossImageResponse;
    }

    public ApiOss getApiOssInfo() {
        List<ApiOssEntity> apiOssEntityList = apiOssRepository.findAll();
        if (CollectionUtils.isEmpty(apiOssEntityList)) {
            return null;
        }
        ApiOssEntity apiOssEntity = apiOssEntityList.get(0);
        String aesKey = apiOssEntity.getEncrypt();
        byte[] aesByte = Base64.decodeBase64(aesKey);
        ApiOss apiOss = new ApiOss();
        BeanUtils.copyProperties(apiOssEntity, apiOss);
        apiOss.setAccessSecret(aesDecode(aesByte, apiOssEntity.getAccessSecret()));
        return apiOss;
    }

    /**
     * AES解密算法
     */
    public static String aesDecode(byte[] bytes, String info) {
        AesCipherService aesCipherService = new AesCipherService();
        return new String(aesCipherService.decrypt(Hex.decode(info), bytes).getBytes());
    }

    private String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        return null;
    }

    public BaseResponse addComment(Integer arenaId, String comment) {
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        ArenaCommentEntity userCommentEntity = new ArenaCommentEntity();
        userCommentEntity.setArenaId(arenaId);
        userCommentEntity.setCommentText(comment);
        userCommentEntity.setCommentTime(new Date());
        userCommentEntity.setUserId(user.getId());
        arenaCommentRepository.save(userCommentEntity);
        return new BaseResponse();
    }
}
