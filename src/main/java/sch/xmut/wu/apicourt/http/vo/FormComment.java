package sch.xmut.wu.apicourt.http.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormComment {
    private Integer id;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("comment_time")
    private String commentTime;
    @JsonProperty("comment_user_name")
    private String commentUserName;
    @JsonProperty("comment_portrait")
    private String commentPortrait;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    public String getCommentPortrait() {
        return commentPortrait;
    }

    public void setCommentPortrait(String commentPortrait) {
        this.commentPortrait = commentPortrait;
    }
}
