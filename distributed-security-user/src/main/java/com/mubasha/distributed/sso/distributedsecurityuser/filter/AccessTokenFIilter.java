//package com.mubasha.distributed.sso.distributedsecurityuser.filter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.MediaType;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Enumeration;
//import java.util.Map;
//
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@Configuration
//public class AccessTokenFIilter extends OncePerRequestFilter {
//
//    @Autowired
//    private TokenStore tokenStore;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String url=request.getRequestURI();
//        if(url.startsWith("client2/agent")){
//            filterChain.doFilter(request, response);
//            return;
//        }
//        String accessTokenValue=null;
//        Enumeration<String> headers = request.getHeaders("Authorization");
//        while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
//            String value = headers.nextElement();
//            if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
//                String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
//                // Add this here for the auth details later. Would be better to change the signature of this method.
//                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
//                        value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
//                int commaIndex = authHeaderValue.indexOf(',');
//                if (commaIndex > 0) {
//                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
//                }
//                accessTokenValue=authHeaderValue;
//            }
//        }
//        if(accessTokenValue!=null){
//            try {
//                OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
//                Map<String, Object> map = accessToken.getAdditionalInformation();
//                String sourceVersion = (String) map.get("loginVersion");
//                String userName = (String) map.get("user_name");
//                String currentVersion = redisTemplate.opsForValue().get("login:" + userName) == null ? null : (String) redisTemplate.opsForValue().get("login:" + userName);
//
//                if (StringUtils.isBlank(sourceVersion) || StringUtils.isBlank(currentVersion) || !sourceVersion.equals(currentVersion)) {
//                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                    PrintWriter writer = response.getWriter();
//                    JSONObject result = new JSONObject();
//                    result.put("code", 800);
//                    result.put("msg", "登录版本异常");
//                    ObjectMapper mapper = new ObjectMapper();
//                    writer.println(mapper.writeValueAsString(result));
//                    writer.flush();
//                    writer.close();
//                    return;
//                }
//            }catch (Exception e){
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                PrintWriter writer = response.getWriter();
//                JSONObject result = new JSONObject();
//                result.put("code", 800);
//                result.put("msg", e.getMessage());
//                ObjectMapper mapper = new ObjectMapper();
//                writer.println(mapper.writeValueAsString(result));
//                writer.flush();
//                writer.close();
//            }
//        }
//        filterChain.doFilter(request, response);
//
//    }
//}
