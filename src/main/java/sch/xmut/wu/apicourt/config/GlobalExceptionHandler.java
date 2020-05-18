package sch.xmut.wu.apicourt.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.ResponseUtil;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import sch.xmut.wu.apicourt.http.response.BaseResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

import static sch.xmut.wu.apicourt.http.response.BaseResponse.FAILD_CODE;
import static sch.xmut.wu.apicourt.http.response.BaseResponse.FAILD_STATUS;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Object badArgumentHandler(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return new BaseResponse(FAILD_CODE,FAILD_STATUS);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Object badArgumentHandler(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return new BaseResponse(FAILD_CODE,FAILD_STATUS);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Object badArgumentHandler(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return new BaseResponse(FAILD_CODE,FAILD_STATUS);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object badArgumentHandler(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return new BaseResponse(FAILD_CODE,FAILD_STATUS);    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public Object badArgumentHandler(ValidationException e) {
        log.error(e.getMessage(), e);
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                String message = ((PathImpl) item.getPropertyPath()).getLeafNode().getName() + item.getMessage();
                return new BaseResponse(FAILD_CODE,FAILD_STATUS);
            }
        }
        return new BaseResponse(FAILD_CODE,FAILD_STATUS);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object seriousHandler(Exception e) {
        log.error(e.getMessage(), e);
        return new BaseResponse(FAILD_CODE,FAILD_STATUS);
    }
}
