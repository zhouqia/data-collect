//package com.ipharmacare.collect.common.bean;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.client.RestTemplate;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class RestTemplateConfig {
//
//    @Bean
//    public RestTemplate restTemplate(){
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setConnectTimeout(3000);
//        factory.setReadTimeout(3000);
//        RestTemplate res = new RestTemplate(factory);
//        for(int i=0;i<res.getMessageConverters().size();i++){
//            if(res.getMessageConverters().get(i) instanceof StringHttpMessageConverter){
//                res.getMessageConverters().set(i,new StringHttpMessageConverter(StandardCharsets.UTF_8));
//            }
//            if(res.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter){
//                List list = new ArrayList<>();
//                list.add(MediaType.ALL);
//                ((MappingJackson2HttpMessageConverter) res.getMessageConverters().get(i)).setSupportedMediaTypes(list);
//            }
//        }
//        return res;
//    }
//
//
//}
