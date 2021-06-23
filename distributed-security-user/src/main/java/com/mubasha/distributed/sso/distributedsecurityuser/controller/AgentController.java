package com.mubasha.distributed.sso.distributedsecurityuser.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/agent")
public class AgentController {


    private static final String accessTokenUri="http://localhost:8080/auth/oauth/token";

    private static final String logOutUri="http://localhost:8080/auth/logout";

    private static final String accessCodeUri="http://localhost:8080/auth/oauth/authorize";
    private String redirectUri="http://10.24.164.102:8082/client2Page/home";
    private String clientId="c2";
    private String clientSecret="secret";


    @GetMapping(value = "/oauth2/getToken/{code}")
    public Result getAccessToken(@PathVariable String code, HttpServletRequest req)
            throws UnsupportedEncodingException, HttpRequestMethodNotSupportedException {
        if (StringUtils.isEmpty(code)) {
            return Result.error("code不能为空");
        }
        String client=req.getParameter("client");
        if(!client.equals("2")){
            return Result.error("客户端不合法");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("client_id", clientId);
        param.add("client_secret", clientSecret);
        param.add("code", code);
        param.add("grant_type", "authorization_code");
        param.add("redirect_uri", redirectUri);
        param.add("scope", "all");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response =restTemplate.postForEntity(accessTokenUri, request, Map.class);
        Map map = response.getBody();
        return Result.success(map);
    }

//    @GetMapping(value = "/oauth2/logout")
//    public Result logOut(){
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
//        ResponseEntity<Map> response =restTemplate.postForEntity(logOutUri, request, Map.class);
//        Map map = response.getBody();
//        return Result.success(map);
//    }


    @GetMapping(value = "/oauth2/refreshToken")
    public Result getAccessToken(HttpServletRequest req)
            throws UnsupportedEncodingException, HttpRequestMethodNotSupportedException {
        String client=req.getParameter("client");
        if(!client.equals("2")){
            return Result.error("客户端不合法");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("client_id", clientId);
        param.add("client_secret", clientSecret);
        param.add("grant_type", "refresh_token");
        param.add("refresh_token",req.getParameter("refresh_token"));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response =restTemplate.postForEntity(accessTokenUri, request, Map.class);
        Map map = response.getBody();
        if(map.get("code")!=null && map.get("code").equals(600)){
            Result result=new Result();
            result.setCode(600);
            result.setMsg(map.get("message").toString());
            return result;
        }
        return Result.success(map);
    }


    @GetMapping(value = "/oauth/authorize")
    public void redirectCode(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String, String[]> paramMap = request.getParameterMap();
        StringBuilder param = new StringBuilder();
        paramMap.forEach((k, v) -> {
            param.append("&").append(k).append("=").append(v[0]);
        });
        response.sendRedirect("http://10.24.164.102:8085/auth/agent/oauth/authorize?"+param.toString());
    }
}
