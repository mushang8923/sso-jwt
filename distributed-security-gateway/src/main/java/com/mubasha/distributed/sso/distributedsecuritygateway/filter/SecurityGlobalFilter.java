package com.mubasha.distributed.sso.distributedsecuritygateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.mubasha.distributed.sso.distributedsecuritygateway.util.ResponseUtils;
import com.nimbusds.jose.JWSObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
