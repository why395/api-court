package sch.xmut.wu.apicourt.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import sch.xmut.wu.apicourt.constant.CacheConstant;
import sch.xmut.wu.apicourt.entity.FormCommentEntity;
import sch.xmut.wu.apicourt.entity.FormEntity;
import sch.xmut.wu.apicourt.entity.UserEntity;
import sch.xmut.wu.apicourt.http.request.FormRequest;
import sch.xmut.wu.apicourt.http.response.BaseResponse;
import sch.xmut.wu.apicourt.http.response.FormResponse;
import sch.xmut.wu.apicourt.http.vo.Form;
import sch.xmut.wu.apicourt.http.vo.FormComment;
import sch.xmut.wu.apicourt.http.vo.User;
import sch.xmut.wu.apicourt.repository.FormCommentRepository;
import sch.xmut.wu.apicourt.repository.FormRepository;
import sch.xmut.wu.apicourt.repository.UserRepository;
import sch.xmut.wu.apicourt.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FormService {
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private FormCommentRepository formCommentRepository;
    @Autowired
    private UserRepository userRepository;

    public BaseResponse formAdd(FormRequest request) {
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        FormEntity formEntity = new FormEntity();
        formEntity.setUserId(user.getId());
        formEntity.setContent(request.getContent());
        if (StringUtils.hasText(request.getCover())) {
            formEntity.setCover(request.getCover());
        }
        formEntity.setTitle(request.getTitle());
        formEntity.setCreateTime(new Date());
        formRepository.save(formEntity);
        return new BaseResponse();
    }

    public FormResponse formDetail(Integer id) {
        FormResponse response = new FormResponse();
        Optional<FormEntity> formEntityOptional = formRepository.findById(id);
        if (!formEntityOptional.isPresent()) {
            response.setStatusCode(BaseResponse.FAILD_CODE);
            response.setMessage("不存在该评论");
            return response;
        }
        FormEntity formEntity = formEntityOptional.get();
        Form form = new Form();
        form.setId(formEntity.getId());
        form.setContent(formEntity.getContent());
        form.setCover(formEntity.getCover());
        form.setTitle(formEntity.getTitle());
        form.setCreateTime(SystemUtils.formatDate(formEntity.getCreateTime()));
        UserEntity userEntity = userRepository.findById(formEntity.getUserId()).get();
        form.setPortrait(userEntity.getPortrait());
        form.setUserName(userEntity.getUserName());
        List<FormCommentEntity> formCommentEntityList = formCommentRepository.findAllByFormIdOrderByCommentTimeDesc(formEntity.getId());
        List<FormComment> formCommentList = new ArrayList<>();
        for (FormCommentEntity formCommentEntity : formCommentEntityList) {
            FormComment formComment = new FormComment();
            formComment.setCommentText(formCommentEntity.getCommentText());
            formComment.setCommentTime(SystemUtils.formatDate(formCommentEntity.getCommentTime()));
            UserEntity userEntity1 = userRepository.findById(formCommentEntity.getUserId()).get();
            formComment.setCommentPortrait(userEntity1.getPortrait());
            formComment.setCommentUserName(userEntity1.getUserName());
            formCommentList.add(formComment);
        }
        form.setFormCommentList(formCommentList);
        form.setCommentNumber("查看" + formCommentList.size() + "条评论");
        response.setVo(form);
        return response;
    }

    public BaseResponse formAddComment(Integer formId, String commentText) {
        Jedis jedis = new Jedis("localhost", 6379);
        User user = JSONObject.parseObject(jedis.get(CacheConstant.USER_INFO_KEY), User.class);
        FormCommentEntity formCommentEntity = new FormCommentEntity();
        formCommentEntity.setCommentText(commentText);
        formCommentEntity.setCommentTime(new Date());
        formCommentEntity.setFormId(formId);
        formCommentEntity.setUserId(user.getId());
        formCommentRepository.save(formCommentEntity);
        return new BaseResponse();
    }

    public FormResponse formList() {
        FormResponse response = new FormResponse();
        List<Form> forms = new ArrayList<>();
        List<FormEntity> formEntityList = formRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        for (FormEntity formEntity : formEntityList) {
            Form form = new Form();
            UserEntity userEntity = userRepository.findById(formEntity.getUserId()).get();
            form.setId(formEntity.getId());
            form.setUserName(userEntity.getUserName());
            form.setPortrait(userEntity.getPortrait());
            form.setCreateTime(SystemUtils.formatDate(formEntity.getCreateTime()));
            form.setContent(formEntity.getContent());
            form.setTitle(formEntity.getTitle());
            form.setCover(formEntity.getCover());
            List<FormCommentEntity> formCommentEntityList = formCommentRepository.findAllByFormIdOrderByCommentTimeDesc(formEntity.getId());
            form.setCommentNumber(String.valueOf(formCommentEntityList.size()));
            forms.add(form);
        }
        response.setFormList(forms);
        return response;
    }
}
