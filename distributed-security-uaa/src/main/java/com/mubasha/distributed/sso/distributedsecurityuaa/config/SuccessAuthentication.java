package com.mubasha.distributed.sso.distributedsecurityuaa.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("successAuthentication")
public class SuccessAuthentication extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        //登录成功设置登录版本号
        String key="login:"+authentication.getName();

        Date now = new Date();
        // 指定格式化格式
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        redisTemplate.opsForValue().set(key,f.format(now));

        JSONObject result=new JSONObject();
        result.put("code",0);
        result.put("msg","登录成功");
        ObjectMapper mapper = new ObjectMapper();
        writer.println(mapper.writeValueAsString(result));
        writer.flush();
        writer.close();
    }
}