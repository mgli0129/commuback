//package com.mg.community.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
///**
// * @ClassName : 类功能
// * @Description : 详细描述
// * @Author : MGLi
// * @Date : 2020/2/29 16:31
// * @Version : v0.0.1
// */
////@SpringBootConfiguration
//public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
//
//    @Autowired
//    private MyAuthenticationFailHandler myAuthenticationFailHandler;
//
//    @Bean
//    UserDetailsService customUserService() {
//        return new MyCustomUserService();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 使用自定义UserDetailsService
//        auth.userDetailsService(customUserService()).passwordEncoder(new BCryptPasswordEncoder());
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin().loginProcessingUrl("/api/user/login")
//                //　自定义的登录验证成功或失败后的去向
//                .successHandler(myAuthenticationSuccessHandler).failureHandler(myAuthenticationFailHandler)
//                // 禁用csrf防御机制(跨域请求伪造)，这么做在测试和开发会比较方便。
//                .and().csrf().disable();
//    }
//
//}
//
