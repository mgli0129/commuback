package com.mg.community.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Value("${file.uploadRoot}")
    private String uploadRoot;

    @Value("${file.uploadPath}")
    private String uploadPath;

    /*
     *  此处需要注入已经定义的拦截器，不能使用new的方式，因为拦截器UserInterceptor已经Spring接管；
     */
    @Autowired
    public UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor).excludePathPatterns("/login").excludePathPatterns(staticAccessPath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("query upload file:" + registry);
        registry.addResourceHandler(staticAccessPath).addResourceLocations("file:" + uploadRoot + uploadPath);
    }

}