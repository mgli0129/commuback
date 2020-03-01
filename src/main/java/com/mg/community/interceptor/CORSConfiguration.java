//package com.mg.community.interceptor;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
///**
// * @ClassName : 跨域配置类
// * @Description : CORS（Cross-Origin Resource Sharing）“跨域资源共享”，是一个W3C标准，它允许浏览器向跨域服务器发送Ajax请求，打破了Ajax只能访问本站内的资源限制
// * @Author : MGLi
// * @Date : 2020/3/1 1:11
// * @Version : v0.0.1
// */
//@Configuration
//public class CORSConfiguration extends WebMvcConfigurationSupport {
//
//    /**
//     * addMapping：配置可以被跨域的路径，可以任意配置，可以具体到直接请求路径。
//     * allowedMethods：允许所有的请求方法访问该跨域资源服务器，如：POST、GET、PUT、DELETE等。
//     * allowedOrigins：允许所有的请求域名访问我们的跨域资源，可以固定单条或者多条内容，如：“http://www.aaa.com”，只有该域名可以访问我们的跨域资源。
//     * allowedHeaders：允许所有的请求header访问，可以自定义设置任意请求头信息。
//     *
//     * @param registry
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedMethods("*")
//                .allowedOrigins("*")
//                .allowedHeaders("*");
//        super.addCorsMappings(registry);
//    }
//}
