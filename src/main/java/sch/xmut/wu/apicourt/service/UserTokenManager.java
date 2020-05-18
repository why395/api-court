package sch.xmut.wu.apicourt.service;


import sch.xmut.wu.apicourt.utils.JwtHelper;

/**
 * 维护用户token
 */
public class UserTokenManager {
    //定义JWT的有效时长
    private static final int TOKEN_VAILDITY_TIME = 30; // 有效时间(分钟)
    //定义允许刷新JWT的有效时长(在这个时间范围内，用户的JWT过期了，不需要重新登录，后台会给一个新的JWT给前端，这个叫Token的刷新机制后面会着重介绍它的意义。)
    private static final int ALLOW_EXPIRES_TIME = 60*24; //  允许过期时间(分钟)



    public static String generateToken(Integer id) {
        JwtHelper jwtHelper = new JwtHelper();
        return jwtHelper.createToken(id);
    }
    public static Integer getUserId(String token) {
        JwtHelper jwtHelper = new JwtHelper();
        Integer userId = jwtHelper.verifyTokenAndGetUserId(token);
        if(userId == null || userId == 0){
            return null;
        }
        return userId;
    }

}
