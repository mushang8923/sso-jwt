package com.mubasha.distributed.sso.distributedsecurityuser.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
public class UserController {

    @GetMapping(value = "/test")
    public String r1(){
        //获取用户身份信息
        //UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String princial= (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  princial+"访问资源2";
    }



}