//package com.mg.community.security;
//
//import com.mg.community.model.User;
//import com.mg.community.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
///**
// * @ClassName : 类功能
// * @Description : 详细描述
// * @Author : MGLi
// * @Date : 2020/2/29 16:44
// * @Version : v0.0.1
// */
//@Component
//public class MyCustomUserService implements UserDetailsService {
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * 登陆验证时，通过username获取用户的所有权限信息
//     * 并返回UserDetails放到spring的全局缓存SecurityContextHolder中，以供授权器使用
//     */
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        MyUserDetails myUserDetails = new MyUserDetails();
//        myUserDetails.setUsername(username);
//        User user = userService.findByAccountId(username);
//        myUserDetails.setPassword(user.getPwd());
//        return myUserDetails;
//    }
//}
