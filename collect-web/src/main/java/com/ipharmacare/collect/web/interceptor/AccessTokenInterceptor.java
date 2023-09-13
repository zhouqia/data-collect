package com.ipharmacare.collect.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AccessTokenInterceptor implements HandlerInterceptor {


    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if(StringUtils.hasLength(request.getHeader("type"))){
//            ThreadLocalUtils.setTypes(request.getHeader("type"));
//        }
//
//        if(StringUtils.hasLength(request.getHeader("dataType"))){
//            ThreadLocalUtils.setDataTypes(request.getHeader("dataType"));
//        }

       /* byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
        String body = new String(bodyBytes, request.getCharacterEncoding());
        ThreadLocalUtils.setSecrets(request.getHeader("secret"));*/
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        ThreadLocalUtils.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
