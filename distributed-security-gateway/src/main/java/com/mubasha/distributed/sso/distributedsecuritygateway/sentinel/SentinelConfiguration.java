package com.mubasha.distributed.sso.distributedsecuritygateway.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.annotation.PostConstruct;

/**
 * @author haoxr
 * @description 自定义网关流控异常响应
 * @createTime 2021/4/11 19:58
 */
@Configuration
@Slf4j
public class SentinelConfiguration {

    @PostConstruct
    private void initBlockHandler() {
        log.info("限流控制");
        BlockRequestHandler blockRequestHandler = (exchange, t) ->
                ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("{'code':'10001','msg':'限流降级'}"));
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

}
