package com.mubasha.distributed.sso.distributedsecurityuaa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mubasha.distributed.sso.distributedsecurityuaa.dao.UserDao;
import com.mubasha.distributed.sso.distributedsecurityuaa.model.MyUserDetails;
import com.mubasha.distributed.sso.distributedsecurityuaa.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class SpringDataUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //根据 账号查询用户信息
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        //将来连接数据库根据账号查询用户信息
//        UserDto userDto = userDao.getUserByUsername(username);
//        if(userDto == null){
//            //如果用户查不到，返回null，由provider来抛出异常
//            return null;
//        }
//        //根据用户的id查询用户的权限
//        List<String> permissions = userDao.findPermissionsByUserId(userDto.getId());
//        //将permissions转成数组
//        String[] permissionArray = new String[permissions.size()];
//        permissions.toArray(permissionArray);
//        //将userDto转成json
//        String principal = JSON.toJSONString(userDto);
//        //UserDetails userDetails=   User.withUsername(username).password(userDto.getPassword()).authorities(permissionArray).build();
//        MyUserDetails myUserDetails=new MyUserDetails(username,userDto.getPassword(), AuthorityUtils.createAuthorityList(permissionArray));
//        myUserDetails.setAddtionObject(principal);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String password=request.getParameter("password");

        //调用iam接口，如果失败则返回return null,成功将password进行加密放进去
        String[] permissionArray = new String[0];

        MyUserDetails myUserDetails=new MyUserDetails(username,passwordEncoder.encode(password), AuthorityUtils.createAuthorityList(permissionArray));
        myUserDetails.setType("user");
        return myUserDetails;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String password=encoder.encode("secret");
        System.out.println(password);
        System.out.println(encoder.matches("secret","$2a$10$sSoTyuXlflCUC7.SVMCFee9mbtdCmnSukp7TQLd3SgAzBKynL5Ocy"));
    }
}
