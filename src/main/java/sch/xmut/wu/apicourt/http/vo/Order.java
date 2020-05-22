package sch.xmut.wu.apicourt.http.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by wu on 2020/05/22
 */
public class Order {
    private Integer id;
    @JsonProperty("arena_court")
    private String arenaCourt;
    @JsonProperty("order_time")
    private String orderTime;
    @JsonProperty("book_long")
    private Double bookLong;
    private Double money;

    public String getArenaCourt() {
        return arenaCourt;
    }

    public void setArenaCourt(String arenaCourt) {
        this.arenaCourt = arenaCourt;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Double getBookLong() {
        return bookLong;
    }

    public void setBookLong(Double bookLong) {
        this.bookLong = bookLong;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
