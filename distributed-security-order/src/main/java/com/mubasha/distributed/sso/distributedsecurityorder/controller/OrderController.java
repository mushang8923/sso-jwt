package com.mubasha.distributed.sso.distributedsecurityorder.controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
public class OrderController {

    @GetMapping(value = "/test")
    public String r1(HttpServletRequest request){
        String payload =request.getHeader("payload");
        //获取用户身份信息
        //UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      //  String princial= (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  payload+"访问资源1";
    }

    @GetMapping(value = "/test1")
    public String r2(HttpServletRequest request) throws InterruptedException {
        String payload =request.getHeader("payload");
        Thread.sleep(5000);
        //获取用户身份信息
        //UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //  String princial= (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  payload+"访问资源2";
    }

    @GetMapping(value = "/login")
    public String login(){
        System.out.println("123123  login");
        return "1111";
    }









}