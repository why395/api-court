package sch.xmut.wu.apicourt.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import sch.xmut.wu.apicourt.http.vo.Form;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormResponse extends BaseResponse<Form> {
    @JsonProperty("form_list")
    private List<Form> formList;

    public List<Form> getFormList() {
        return formList;
    }

    public void setFormList(List<Form> formList) {
        this.formList = formList;
    }
}
