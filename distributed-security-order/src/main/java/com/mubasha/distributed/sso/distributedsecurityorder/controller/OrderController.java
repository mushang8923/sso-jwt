package com.mubasha.distributed.sso.distributedsecurityorder.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
public class OrderController {

    @GetMapping(value = "/test")
    public String r1(){
        //获取用户身份信息
        //UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String princial= (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  princial+"访问资源1";
    }

    @GetMapping(value = "/login")
    public String login(){
        System.out.println("123123  login");
        return "1111";
    }









}