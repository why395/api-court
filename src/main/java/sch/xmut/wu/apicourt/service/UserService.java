package sch.xmut.wu.apicourt.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import sch.xmut.wu.apicourt.constant.CacheConstant;
import sch.xmut.wu.apicourt.entity.ArenaEntity;
import sch.xmut.wu.apicourt.entity.CourtEntity;
import sch.xmut.wu.apicourt.entity.UserBookEntity;
import sch.xmut.wu.apicourt.entity.UserCollectEntity;
import sch.xmut.wu.apicourt.entity.UserEntity;
import sch.xmut.wu.apicourt.http.request.UserBookRequest;
import sch.xmut.wu.apicourt.http.request.UserCollectRequest;
import sch.xmut.wu.apicourt.http.request.UserRequest;
import sch.xmut.wu.apicourt.http.response.BaseResponse;
import sch.xmut.wu.apicourt.http.response.LayerResponse;
import sch.xmut.wu.apicourt.http.response.UserBookResponse;
import sch.xmut.wu.apicourt.http.response.UserCollectResponse;
import sch.xmut.wu.apicourt.http.vo.Arena;
import sch.xmut.wu.apicourt.http.vo.Order;
import sch.xmut.wu.apicourt.http.vo.User;
import sch.xmut.wu.apicourt.repository.ArenaRepository;
import sch.xmut.wu.apicourt.repository.CourtRepository;
import sch.xmut.wu.apicourt.repository.UserBookRepository;
import sch.xmut.wu.apicourt.repository.UserCollectRepository;
import sch.xmut.wu.apicourt.repository.UserRepository;
import sch.xmut.wu.apicourt.utils.SystemUtils;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by wu on 2020/04/13
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserBookRepository userBookRepository;
    @Autowired
    private UserCollectRepository userCollectRepository;
    @Autowired
    private ArenaRepository arenaRepository;
    @Autowired
    private CourtRepository courtRepository;

    public BaseResponse saveUserInfo(UserRequest request) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setCreateTime(new Date());
        userRepository.save(userEntity);
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.set(CacheConstant.USER_INFO_KEY, JSONObject.toJSONString(user));
        return new BaseResponse();
    }

    public BaseResponse book(UserBookRequest request) {
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        UserBookEntity userBookEntity = new UserBookEntity();
        BeanUtils.copyProperties(request, userBookEntity);
        userBookEntity.setUserId(user.getId());
        userBookRepository.save(userBookEntity);
        return new BaseResponse();
    }

    public BaseResponse collectArena(UserCollectRequest request) {
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        UserCollectEntity userCollectEntity = new UserCollectEntity();
        userCollectEntity.setArenaId(request.getArenaId());
        userCollectEntity.setCollectTime(new Date());
        userCollectEntity.setUserId(user.getId());
        userCollectRepository.save(userCollectEntity);
        return new BaseResponse();
    }

    public UserCollectResponse collectList() {
        UserCollectResponse response = new UserCollectResponse();
        List<Arena> arenaList = new ArrayList<>();
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        List<UserCollectEntity> userCollectEntityList = userCollectRepository.findAllByUserId(user.getId());
        for (UserCollectEntity userCollectEntity : userCollectEntityList) {
            Arena arena = new Arena();
            Optional<ArenaEntity> arenaEntity = arenaRepository.findById(userCollectEntity.getArenaId());
            if (arenaEntity.isPresent()) {
                BeanUtils.copyProperties(arenaEntity.get(), arena);
            }
            arenaList.add(arena);
        }
        response.setArenaList(arenaList);
        return response;
    }

    public LayerResponse getUserList(Pageable pageable) {
        Page<UserEntity> pageList = userRepository.findAll(pageable);
        List<User> userList = new ArrayList<>();
        for (UserEntity userEntity : pageList) {
            User user = new User();
            BeanUtils.copyProperties(userEntity, user);
            userList.add(user);
        }
        List<User> list = convertToUserList(userRepository.findAll());//所有用户数 用于获取数量
        LayerResponse response = new LayerResponse();
        response.setData(userList);
        response.setCount(list.size());
        return response;
    }

    private List<User> convertToUserList(List<UserEntity> userEntityList) {
        if (CollectionUtils.isEmpty(userEntityList))
            return null;
        List<User> list = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            User user = new User();
            BeanUtils.copyProperties(userEntity, user);
            list.add(user);
        }
        return list;
    }

    public LayerResponse findUser(UserRequest userRequest) {
        LayerResponse response = new LayerResponse();
        List<UserEntity> userEntityList = userRepository.findUser(userRequest.getUserName(), userRequest.getWechatNumber());
        List<User> userList = convertToUserList(userEntityList);
        if (CollectionUtils.isEmpty(userList)) {
            response.setMsg("查询不到用户信息");
            return response;
        }
        response.setData(userList);
        response.setCount(userList.size());
        return response;
    }


    /**
     * 这里本身是取openId，但是字段名称不一致。所以查找wechat_number
     * @param openId
     * @return
     */
    public UserEntity  queryByOid(String openId){
        Specification<UserEntity> specification = new Specification<UserEntity>() {
            @Override
            public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Object> openIdT = root.get("wechatNumber");
                Predicate predicate = criteriaBuilder.equal(openIdT, openId);
                return predicate;
            }
        };
        List<UserEntity> userGroups = userRepository.findAll(specification);
        if(!CollectionUtils.isEmpty(userGroups)) {
            return userGroups.get(0);
        }else {
            return null;
        }
    }

    public UserBookResponse orderList() {
        UserBookResponse response = new UserBookResponse();
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        List<UserBookEntity> userBookEntityList = userBookRepository.findAllByUserId(user.getId());
        List<Order> orderList = new ArrayList<>();
        for (UserBookEntity userBookEntity : userBookEntityList) {
            Order order = new Order();
            order.setId(userBookEntity.getId());
            order.setBookLong(Double.valueOf(String.format("%.1s", userBookEntity.getBookLong()/60)));
            CourtEntity courtEntity = courtRepository.getOne(userBookEntity.getCourtId());
            if (courtEntity != null) {
                Optional<ArenaEntity> arenaEntityOptional = arenaRepository.findById(courtEntity.getArenaId());
                if (arenaEntityOptional.isPresent()) {
                    order.setArenaCourt(arenaEntityOptional.get().getName() + "(" + courtEntity.getCourtName() + ")");
                }
            }
            order.setMoney(Double.valueOf(String.format("%.1s", userBookEntity.getMoney())));
            order.setOrderTime(SystemUtils.formatDate(userBookEntity.getBookTime()));
            orderList.add(order);
        }
        response.setOrderList(orderList);
        return response;
    }
}
