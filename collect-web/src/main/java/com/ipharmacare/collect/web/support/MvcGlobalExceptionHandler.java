//package com.ipharmacare.collect.web.support;
//
//import com.ipharmacare.collect.common.core.exception.CollectBadRequestException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
//import org.springframework.web.multipart.MultipartException;
//import org.springframework.web.servlet.NoHandlerFoundException;
//
//
//@RestControllerAdvice
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@Slf4j
//public class MvcGlobalExceptionHandler {
//
//
//    @ExceptionHandler(value = CollectAuthException.class)
//    public CollectResponse handleIphmsAuthException(CollectAuthException e) {
//        log.error("数据采集平台权限异常", e);
//        return CollectResponse.createByMessage(HttpStatus.UNAUTHORIZED, e.getMessage());
//    }
//
//    @ExceptionHandler(value = CollectBadRequestException.class)
//    public CollectResponse handleIphmsBadRequestException(CollectBadRequestException e) {
//        log.warn("数据采集平台请求不合法", e);
//        return CollectResponse.createByMessage(HttpStatus.BAD_REQUEST, e.getMessage());
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public CollectResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        log.warn("数据采集平台请求参数错误", e);
//        return CollectResponse.createByMessage(HttpStatus.BAD_REQUEST, e.getBindingResult().getFieldError().getDefaultMessage());
//    }
//
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public CollectResponse handlerNoFoundException(Exception e) {
//        log.warn(e.getMessage(), e);
//        return CollectResponse.createByMessage(HttpStatus.METHOD_NOT_ALLOWED, "路径不存在,请检查路径是否正确");
//    }
//
//    @ExceptionHandler(MultipartException.class)
//    public CollectResponse exception(MaxUploadSizeExceededException e) {
//        log.warn("数据采集平台上传错误", e);
//        return CollectResponse.createByMessage(HttpStatus.BAD_REQUEST, "文件上传大小不能超过5M!");
//    }
//}
