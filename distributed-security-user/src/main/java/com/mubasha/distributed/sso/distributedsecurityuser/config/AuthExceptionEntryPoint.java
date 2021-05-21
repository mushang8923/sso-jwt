package com.mubasha.distributed.sso.distributedsecurityuser.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component("unauthorizedEntryPoint")
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    private static final Log logger = LogFactory.getLog(AuthExceptionEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = httpServletResponse.getWriter();
            JSONObject result=new JSONObject();

            if(e instanceof InsufficientAuthenticationException){
                  if(e.getCause() instanceof InvalidTokenException){
                      if(e.getCause().getMessage().startsWith("Access token expired:")){
                          result.put("code",700);
                      }else{
                          result.put("code",800);
                      }
                      result.put("msg",e.getCause().getMessage());
                  }else{
                      result.put("code",800);
                      result.put("msg",e.getMessage());
                  }
            }else{
                result.put("code",800);
                result.put("msg","未授权，请求登录");
            }
            ObjectMapper mapper = new ObjectMapper();
            writer.println(mapper.writeValueAsString(result));
            writer.flush();
            writer.close();
    }
}

