package com.mubasha.distributed.sso.distributedsecurityuaa.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/agent")
public class AgentController {


    private static final String accessTokenUri = "http://localhost:8080/auth/oauth/token";


    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private TokenEndpoint tokenEndpoint;


    @GetMapping(value = "/oauth2/getToken/{code}")
    public Result getAccessToken(@PathVariable String code, HttpServletRequest req)
            throws UnsupportedEncodingException, HttpRequestMethodNotSupportedException {
        Map<String, String[]> paramMap = req.getParameterMap();
        StringBuilder params = new StringBuilder();
        StringBuilder targetProCode = new StringBuilder();
        paramMap.forEach((k, v) -> {
            params.append("&").append(k).append("=").append(v[0]);
            if (k.equals("client")) {
                targetProCode.append(v[0]);
            }
        });


        if (StringUtils.isEmpty(code)) {
            return Result.error("code不能为空");
        }

        ClientDetails clientDetails = null;
        if (targetProCode.toString().equals("c1")) {
            clientDetails = clientDetailsService.loadClientByClientId("1");
        } else if (targetProCode.toString().equals("c2")) {
            clientDetails = clientDetailsService.loadClientByClientId("2");
        }

        if (clientDetails == null) {
            return Result.error("客户端不合法");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("client_id", clientDetails.getClientId());
        param.add("client_secret", clientDetails.getClientSecret());
        param.add("code", code);
        param.add("grant_type", "authorization_code");
        param.add("redirect_uri", clientDetails.getRegisteredRedirectUri().toString());
        param.add("scope", "all");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, request, Map.class);
        Map map = response.getBody();
        if (map.get("code") != null && map.get("code").equals(600)) {
            Result result = new Result();
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
        StringBuilder targetProCode = new StringBuilder();

        paramMap.forEach((k, v) -> {
            param.append("&").append(k).append("=").append(v[0]);
            if (k.equals("client")) {
                targetProCode.append(v[0]);
            }
        });
        ClientDetails clientDetails = null;
        if (targetProCode.toString().equals("1")) {
            clientDetails = clientDetailsService.loadClientByClientId("c1");
        } else if (targetProCode.toString().equals("2")) {
            clientDetails = clientDetailsService.loadClientByClientId("c2");
        }

        if (clientDetails == null) {
            return;
        }
        Set<String> registerRedirectUrl=clientDetails.getRegisteredRedirectUri();
        String url=registerRedirectUrl.iterator().next();

        StringBuilder redirectUrl = new StringBuilder();
        redirectUrl.append("http://10.24.164.113:8080/auth/oauth/authorize");
        redirectUrl.append("?client_id=");
        redirectUrl.append(clientDetails.getClientId());
        redirectUrl.append("&redirect_uri=");
        redirectUrl.append(url);
        redirectUrl.append("&response_type=code");
        redirectUrl.append(param.toString());
        response.sendRedirect(redirectUrl.toString());
    }
}
