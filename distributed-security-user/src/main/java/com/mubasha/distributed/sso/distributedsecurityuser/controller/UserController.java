package com.mubasha.distributed.sso.distributedsecurityuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
public class UserController {

    @GetMapping(value = "/test")
    public String r1(HttpServletRequest request){
        //获取用户身份信息
        String payload =request.getHeader("payload");
        //UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //String princial= (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return   payload+"访问资源2";
    }



}