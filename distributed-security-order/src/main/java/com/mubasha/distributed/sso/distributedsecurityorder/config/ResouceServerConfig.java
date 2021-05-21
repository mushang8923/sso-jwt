package com.mubasha.distributed.sso.distributedsecurityorder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
@EnableResourceServer
public class ResouceServerConfig extends ResourceServerConfigurerAdapter {


    public static final String RESOURCE_ID = "res1";

    @Autowired
    TokenStore tokenStore;

    @Autowired
    private AuthExceptionEntryPoint authExceptionEntryPoint;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID)//资源 id
                .tokenStore(tokenStore)
                .authenticationEntryPoint(authExceptionEntryPoint)
//                .tokenServices(tokenService())//验证令牌的服务
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/agent/oauth2/getToken/**","/agent/oauth/authorize","/agent/oauth2/refreshToken","/oauth2/logout").permitAll()
                .antMatchers("/**").access("#oauth2.hasScope('all')")
                .and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authExceptionEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }



}
