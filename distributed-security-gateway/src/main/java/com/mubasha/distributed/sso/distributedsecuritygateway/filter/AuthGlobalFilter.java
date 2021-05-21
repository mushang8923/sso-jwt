package com.mubasha.distributed.sso.distributedsecuritygateway.filter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
@Slf4j
@AllArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//
         String a=ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(Authentication::getPrincipal).toString();
        System.out.println(123123);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (!(authentication instanceof OAuth2Authentication)) {
//            return null;
//        }
//        String token = exchange.getRequest().getHeaders().getFirst(AuthConstants.JWT_TOKEN_HEADER);
//        if (StringUtils.isBlank(token)) {
//            return chain.filter(exchange);
//        }
//        token = token.replace(AuthConstants.JWT_TOKEN_PREFIX, Strings.EMPTY);
//        JWSObject jwsObject = JWSObject.parse(token);
//        String payload = jwsObject.getPayload().toString();
//
//        // 黑名单token(登出、修改密码)校验
//        JSONObject jsonObject = JSONUtil.parseObj(payload);
//        String jti = jsonObject.getStr("jti"); // JWT唯一标识
//
//        Boolean isBlack = redisTemplate.hasKey(AuthConstants.TOKEN_BLACKLIST_PREFIX + jti);
//        if (isBlack) {
//            ServerHttpResponse response = exchange.getResponse();
//            response.setStatusCode(HttpStatus.OK);
//            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//            response.getHeaders().set("Access-Control-Allow-Origin", "*");
//            response.getHeaders().set("Cache-Control", "no-cache");
//            String body = JSONUtil.toJsonStr(Result.custom(ResultCode.INVALID_TOKEN_OR_EXPIRED));
//            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
//            return response.writeWith(Mono.just(buffer));
//        }
//
//        ServerHttpRequest request = exchange.getRequest().mutate()
//                .header(AuthConstants.JWT_PAYLOAD_KEY, payload)
//                .build();
//        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
