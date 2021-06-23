package com.mubasha.distributed.sso.distributedsecurityuaa.config;

import com.mubasha.distributed.sso.distributedsecurityuaa.model.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer  implements TokenEnhancer {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        String key="login:"+authentication.getName();
        String version= (String) redisTemplate.opsForValue().get(key);
        additionalInfo.put("loginVersion",version );
        // 注意添加的额外信息，最好不要和已有的json对象中的key重名，容易出现错误
        //additionalInfo.put("authorities", user.getAuthorities());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}
