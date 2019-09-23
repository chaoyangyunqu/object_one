package com.shineyue.certSign.handle;

import com.shineyue.certSign.model.DataResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebResult;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * @Description: TODO 全局异常处理
 * @author: luofuwei
 * @date: wrote on 2019/9/23
 */
@ControllerAdvice
public class GlobalExeceptionHandle {
    /**
     * 用来处理bean validation异常
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public DataResult resolveConstraintViolationException(ConstraintViolationException ex){
        DataResult dataResult = new DataResult();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        if(!CollectionUtils.isEmpty(constraintViolations)){
            StringBuilder msgBuilder = new StringBuilder();
            for(ConstraintViolation constraintViolation :constraintViolations){
                msgBuilder.append(constraintViolation.getMessage()).append(",");
            }
            String errorMessage = msgBuilder.toString();
            if(errorMessage.length()>1){
                errorMessage = errorMessage.substring(0,errorMessage.length()-1);
            }
            dataResult.setError(errorMessage);
            return dataResult;
        }
        dataResult.setMsg(ex.getMessage());
        return dataResult;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public DataResult resolveMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        DataResult dataResult = new DataResult();
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
        if(!CollectionUtils.isEmpty(objectErrors)) {
            StringBuilder msgBuilder = new StringBuilder();
            for (ObjectError objectError : objectErrors) {
                msgBuilder.append(objectError.getDefaultMessage()).append(",");
            }
            String errorMessage = msgBuilder.toString();
            if (errorMessage.length() > 1) {
                errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
            }
            dataResult.setError(errorMessage);
            return dataResult;
        }
        dataResult.setMsg(ex.getMessage());
        return dataResult;
    }

}
