package sch.xmut.wu.apicourt.web.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import sch.xmut.wu.apicourt.constant.CacheConstant;
import sch.xmut.wu.apicourt.entity.UserEntity;
import sch.xmut.wu.apicourt.http.dto.UserDto;
import sch.xmut.wu.apicourt.http.dto.WxLoginInfo;
import sch.xmut.wu.apicourt.http.request.UserBookRequest;
import sch.xmut.wu.apicourt.http.request.UserCollectRequest;
import sch.xmut.wu.apicourt.http.request.UserRequest;
import sch.xmut.wu.apicourt.http.response.BaseResponse;
import sch.xmut.wu.apicourt.http.response.UserCollectResponse;
import sch.xmut.wu.apicourt.service.UserService;
import sch.xmut.wu.apicourt.service.UserTokenManager;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wu on 2020/04/13
 */
@Slf4j
@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private WxMaService wxService;

    @Autowired
    private UserService userService;

    //用户微信登录时，保存用户信息到数据库中 request：user_name、wechat_number、portrait
    @PostMapping(value = "/save/user-info")
    @ResponseBody
    public BaseResponse saveUserInfo(@RequestBody UserRequest request) {
        return userService.saveUserInfo(request);
    }

    //预定球场 request：court_id、book_time、book_long、money
    @PostMapping(value = "/book-court")
    @ResponseBody
    public BaseResponse book(@RequestBody UserBookRequest request) {
        return userService.book(request);
    }

    //收藏球馆 request：arena_id
    @PostMapping(value = "/collect-arena")
    @ResponseBody
    public BaseResponse collectArena(@RequestBody UserCollectRequest request) {
        return userService.collectArena(request);
    }

    //收藏的球馆列表
    @PostMapping(value = "/collect-list")
    @ResponseBody
    public UserCollectResponse collectList() {
        return userService.collectList();
    }



    @PostMapping("login")
    @ResponseBody
    public Object loginByWeixin(@RequestBody WxLoginInfo wxLoginInfo, HttpServletRequest request) {
        String code = wxLoginInfo.getCode();
        UserDto userDto = wxLoginInfo.getUserInfo();
        if (code == null || userDto == null) {
            throw new  IllegalArgumentException();
        }

        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(code);
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            throw new  IllegalArgumentException();
        }

        UserEntity user = userService.queryByOid(openId);
        if (user == null) {
            UserRequest userRequest =new UserRequest();
            userRequest.setUserName(userDto.getNickName());
            userRequest.setWechatNumber(openId);
            userRequest.setPortrait(userDto.getAvatarUrl());
            userService.saveUserInfo(userRequest);

        }

        UserEntity user1 = userService.queryByOid(openId);
        // token
        String token = UserTokenManager.generateToken(user1.getId());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userDto);
        result.put("userId", user1.getId());
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.set(CacheConstant.USER_INFO_KEY, JSON.toJSONString(user1));
        return BaseResponse.Success(result);
    }
}
