package com.zhangjun.gulimall.gateway.config;


import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.zhangjun.common.exception.BizCodeEnume;
import com.zhangjun.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-10 10:32
 */
@Configuration
public class SentinelGateWayConfig {

    public SentinelGateWayConfig(){
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                String errorJson = JSON.toJSONString(R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg()));
                return ServerResponse.ok().body(
                        Mono.just(errorJson),String.class
                );
            }
        });
    }
}
