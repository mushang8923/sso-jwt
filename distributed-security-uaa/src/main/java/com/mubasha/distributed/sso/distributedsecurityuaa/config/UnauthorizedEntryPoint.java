package com.mubasha.distributed.sso.distributedsecurityuaa.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Component("unauthorizedEntryPoint")
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Map<String, String[]> paramMap = request.getParameterMap();
        StringBuilder targetProCode=new StringBuilder();
        paramMap.forEach((k, v) -> {
            if (k.equals("client")) {
                targetProCode.append(k).append("=").append(v[0]);
            }
        });
        response.sendRedirect("http://10.24.164.97:8085/login?"+targetProCode.toString());
    }
}
