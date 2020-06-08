/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: OrderService
 * Author:   qitiandasheng
 * Date:     2020/5/15 22:32
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sch.xmut.wu.apicourt.service;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sch.xmut.wu.apicourt.entity.ArenaEntity;
import sch.xmut.wu.apicourt.entity.CourtEntity;
import sch.xmut.wu.apicourt.entity.UserBookEntity;
import sch.xmut.wu.apicourt.entity.UserEntity;
import sch.xmut.wu.apicourt.http.request.UserBookRequest;
import sch.xmut.wu.apicourt.http.response.LayerResponse;
import sch.xmut.wu.apicourt.http.vo.Court;
import sch.xmut.wu.apicourt.repository.UserBookRepository;
import sch.xmut.wu.apicourt.repository.UserRepository;
import sch.xmut.wu.apicourt.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private UserBookRepository userBookRepository;
    @Autowired
    private UserRepository userRepository;

    public LayerResponse getOrderList(Pageable pageable){
        LayerResponse response = new LayerResponse();
        List<UserBookRequest> userBookRequestList = new ArrayList<>();
        Page<UserBookEntity> userbookEntityPage = userBookRepository.findAll(pageable);
        for (UserBookEntity userbookEntity : userbookEntityPage) {
            UserBookRequest userbookrequest = new UserBookRequest();
            BeanUtils.copyProperties(userbookEntity, userbookrequest);
            userbookrequest.setBookTime(SystemUtils.formatDate(userbookEntity.getBookTime()));
            Optional<UserEntity> optional = userRepository.findById(userbookEntity.getUserId());    //通过arena_id找球馆
            if (optional.isPresent()) {
                userbookrequest.setUserName(optional.get().getUserName());
            }
            userBookRequestList.add(userbookrequest);
        }
        response.setData(userBookRequestList);
        response.setCount((int) userBookRepository.count());
        return response;
    }
}

