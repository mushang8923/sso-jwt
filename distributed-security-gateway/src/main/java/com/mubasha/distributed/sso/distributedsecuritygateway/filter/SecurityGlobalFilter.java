package com.mubasha.distributed.sso.distributedsecuritygateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mubasha.distributed.sso.distributedsecuritygateway.util.ResponseUtils;
import com.nimbusds.jose.JWSObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全拦截全局过滤器
 *
 * @author haoxr
 * @date 2020-06-12
 */
@Component
@Slf4j
public class SecurityGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate redisTemplate;

    // 是否演示环境
//    @Value("${demo}")
//    private Boolean isDemoEnv;

    private static Map<String,MyClientDetail> map =new HashMap<>();
    static {
        MyClientDetail myClientDetail=new MyClientDetail();
        myClientDetail.setClientId("c1");
        myClientDetail.setSecret("secret");
        myClientDetail.setScope("all");
        myClientDetail.setRedirect_uri("http://10.24.164.97:8081/client1Page/home");

        MyClientDetail myClientDetail1=new MyClientDetail();
        myClientDetail1.setClientId("c2");
        myClientDetail1.setSecret("secret");
        myClientDetail1.setScope("all");
        myClientDetail1.setRedirect_uri("http://10.24.164.97:8082/client2Page/home");
        map.put("c1",myClientDetail);
        map.put("c2",myClientDetail1);
    }

    @Data
    static class MyClientDetail{
        private String clientId;
        private String secret;
        private String scope;
        private String redirect_uri;
    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 演示环境禁止删除和修改
//        if (isDemoEnv
//                && (HttpMethod.DELETE.toString().equals(request.getMethodValue()) // 删除方法
//                || HttpMethod.PUT.toString().equals(request.getMethodValue())) // 修改方法
//        ) {
//            return ResponseUtils.writeErrorInfo(response, ResultCode.FORBIDDEN_OPERATION);
//        }
        if(request.getURI().getPath().startsWith("/auth")){
            if(request.getURI().getPath().contains("/oauth/authorize") || request.getURI().getPath().contains("/oauth/token")) {

                String clientId = request.getQueryParams().getFirst("client");
                MyClientDetail myClientDetail=map.get(clientId);

                String orgPath = request.getURI().toString();
                StringBuilder targetPath = new StringBuilder();
                targetPath.append("&client_id=").append(clientId);
                targetPath.append("&redirect_uri=").append(myClientDetail.getRedirect_uri());
                if(request.getURI().getPath().contains("/oauth/authorize")) {
                    targetPath.append("&response_type=code");
                    targetPath.append("&scope").append(myClientDetail.getScope());
                }
                if(request.getURI().getPath().contains("/oauth/token")){
                    http://localhost:53020/uaa/oauth/token?grant_type=refresh_token&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ6aGFuZ3NhbiIsInNjb3BlIjpbIlJPTEVfQURNSU4iXSwiYXRpIjoiN2RhZjc3YjMtMWM4NC00YjNhLThlZjEtNzJmOWZhMGJlYmZmIiwiZXhwIjoxNjIyMzQwMTM2LCJhdXRob3JpdGllcyI6WyJwMSIsInAzIl0sImp0aSI6ImYzN2RiYzBkLTM2MTUtNDYwZC04YjRhLTNjMjRjYzc3NTc0NiIsImNsaWVudF9pZCI6ImNsaWVudDEifQ.AbiKA5qU1Lm2ijQF1UoXAwV89XV-HME7sgiFgBzr7K0&client_id=client1&client_secret=secret1
                    if(request.getURI().getPath().contains("grant_type=refresh_token")){
                        targetPath.append("&client_secret=").append(myClientDetail.getSecret());
                    }else{
                        //http://localhost:53010/auth/oauth/token?client_id=c1&client_secret=secret&grant_type=authorization_code&code=C5Lyla&redirect_uri=http://10.24.164.102:8081/client1Page/home
                        targetPath.append("&client_secret=").append(myClientDetail.getSecret());
                        targetPath.append("&grant_type=authorization_code");
                        targetPath.append("&code=").append(request.getQueryParams().getFirst("code"));
                    }

                }

                String targetPathStr = orgPath + targetPath.toString();
                request = exchange.getRequest().mutate()
                        .uri(new URI(targetPathStr))
                        .build();
                exchange = exchange.mutate().request(request).build();
            }
            return chain.filter(exchange);
        }

        // 非JWT或者JWT为空不作处理
        String token = request.getHeaders().getFirst("Authorization");
        if (StrUtil.isBlank(token) || !token.startsWith("Bearer ")) {
//            return chain.filter(exchange);
            JSONObject result=new JSONObject();
            result.put("code", 800);
            result.put("msg","非法访问");
            return ResponseUtils.writeErrorInfo(response, result);
        }

        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在拦截响应token失效
        token = token.replace("Bearer ", Strings.EMPTY);
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(payload);
        System.out.println("解析到的jsonObject 报文：{}"+jsonObject.toString());


        //验证用户当前登录版本号与jwt的登录版本号是否一致
        String sourceVersion = (String) jsonObject.get("loginVersion");
        String userName = (String) jsonObject.get("user_name");
        String currentVersion = redisTemplate.opsForValue().get("login:" + userName) == null ? null : (String) redisTemplate.opsForValue().get("login:" + userName);

        if (StringUtils.isBlank(sourceVersion) || StringUtils.isBlank(currentVersion) || !sourceVersion.equals(currentVersion)) {
            JSONObject result=new JSONObject();
            result.put("code", 800);
            result.put("msg", "登录版本异常");
            return ResponseUtils.writeErrorInfo(response, result);
        }

//        if (isBlack) {
//            return ResponseUtils.writeErrorInfo(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
//        }

        // 存在token且不是黑名单，request写入JWT的载体信息
        request = exchange.getRequest().mutate()
                .header("payload", payload)
                .build();
        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
