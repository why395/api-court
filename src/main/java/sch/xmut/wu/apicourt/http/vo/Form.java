package sch.xmut.wu.apicourt.http.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Form {
    private Integer id;
    private String title;
    private String content;
    private String cover;
    @JsonProperty("create_time")
    private String createTime;
    private String portrait;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("comment_number")
    private String commentNumber;
    @JsonProperty("form_comment_list")
    private List<FormComment> formCommentList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public List<FormComment> getFormCommentList() {
        return formCommentList;
    }

    public void setFormCommentList(List<FormComment> formCommentList) {
        this.formCommentList = formCommentList;
    }
}
