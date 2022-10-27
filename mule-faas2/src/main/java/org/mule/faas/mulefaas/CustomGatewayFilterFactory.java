package org.mule.faas.mulefaas;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;

import java.util.function.Consumer;

public class CustomGatewayFilterFactory implements GatewayFilterFactory {
    @Override
    public GatewayFilter apply(String routeId, Consumer consumer) {
        return GatewayFilterFactory.super.apply(routeId, consumer);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return null;
    }
}
