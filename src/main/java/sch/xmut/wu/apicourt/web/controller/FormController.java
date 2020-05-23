package sch.xmut.wu.apicourt.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sch.xmut.wu.apicourt.http.request.FormRequest;
import sch.xmut.wu.apicourt.http.response.BaseResponse;
import sch.xmut.wu.apicourt.http.response.FormResponse;
import sch.xmut.wu.apicourt.service.FormService;

@Controller
@RequestMapping("/form")
public class FormController {
    @Autowired
    private FormService formService;

    //发帖
    @PostMapping("/add")
    @ResponseBody
    public BaseResponse formAdd(@RequestBody FormRequest request) {
        return formService.formAdd(request);
    }

    //帖子详情
    @GetMapping("/detail")
    @ResponseBody
    public FormResponse formDetail(@RequestParam Integer id) {
        return formService.formDetail(id);
    }

    //发表评论
    @GetMapping("/add-comment")
    @ResponseBody
    public BaseResponse formAddComment(@RequestParam("form_id") Integer formId, @RequestParam("comment_text") String commentText) {
        return formService.formAddComment(formId, commentText);
    }

    //帖子列表
    @PostMapping("/list")
    @ResponseBody
    public FormResponse formList() {
        return formService.formList();
    }
}
