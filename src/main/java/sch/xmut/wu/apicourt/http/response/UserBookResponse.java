package sch.xmut.wu.apicourt.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import sch.xmut.wu.apicourt.http.vo.Order;
import java.util.List;

/**
 * Created by wu on 2020/05/22
 */
public class UserBookResponse extends BaseResponse {
    @JsonProperty("order_list")
    private List<Order> orderList;

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
