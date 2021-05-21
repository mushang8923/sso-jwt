package com.mubasha.distributed.sso.distributedsecurityuaa.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();

        String key="login:"+authentication.getName();
        redisTemplate.delete(key);

        JSONObject result=new JSONObject();
        result.put("code",0);
        result.put("msg","退出成功");
        ObjectMapper mapper = new ObjectMapper();
        writer.println(mapper.writeValueAsString(result));
        writer.flush();
        writer.close();
    }
}
