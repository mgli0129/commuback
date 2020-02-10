package com.mg.community.service.impl;

import com.mg.community.mapper.UserMapper;
import com.mg.community.model.User;
import com.mg.community.model.UserExample;
import com.mg.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller("UserService")
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserMapper userMapper;

    /**
     * 通过外部网站的account id查询用户信息；
     *
     * @param accountId
     * @return
     */
    @Override
    public User findByAccountId(String accountId) {

        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(accountId);
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList.size() == 0){
            return null;
        }
        return userList.get(0);
    }

    /**
     * 通过token获取User
     *
     * @param token
     * @return
     */
    @Override
    public User findByToken(String token) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTokenEqualTo(token);
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList.size() ==0){
            return null;
        }
        return userList.get(0);
    }

    /**
     * 通过ID获取User
     *
     * @param id
     * @return
     */
    @Override
    public User findById(Long id) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(id);
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size()>0){
            return users.get(0);
        }
        return null;
    }

    /**
     * 新增或修改User
     *
     * @param user
     */
    @Override
    public void createOrUpdate(User user) {

        if(user.getId() == null){
            //insert
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else{
            //update
            user.setGmtModified(System.currentTimeMillis());
            UserExample userExample = new UserExample();
            userExample.createCriteria().andIdEqualTo(user.getId());
            userMapper.updateByExampleSelective(user,userExample);
        }
    }

    @Override
    public User findByName(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(username);
        List<User> userList = userMapper.selectByExample(userExample);
        if(userList.size() ==0){
            return null;
        }
        return userList.get(0);
    }

    @Override
    public List<User> listByIds(List<Long> userIds) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        return userMapper.selectByExample(userExample);
    }
}
