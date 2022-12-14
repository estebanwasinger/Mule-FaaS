/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package org.mule.faas.mulefaas.routing;

import org.mule.faas.mulefaas.ApplicationLifeCycleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A gateway route resolver which is used for dynamically refresh routes during the application runtime.
 *
 * @author          boto
 * Creation Date    25th June 2018
 */
@Component
public class RefreshableRoutesLocator implements RouteLocator {

    private final RouteLocatorBuilder builder;
    private final GatewayRoutesRefresher gatewayRoutesRefresher;
    private ApplicationLifeCycleManager applicationLifeCycleManager;

    private RouteLocatorBuilder.Builder routesBuilder;
    private Flux<Route> route = Flux.empty();
    private ApplicationLifeCycleManager lifeCycle;

    @Autowired
    public RefreshableRoutesLocator(@NonNull final RouteLocatorBuilder builder,
                                    @NonNull final GatewayRoutesRefresher gatewayRoutesRefresher) {
        this.builder = builder;
        this.gatewayRoutesRefresher = gatewayRoutesRefresher;
        clearRoutes();
    }

    /**
     * Remove all routes.
     */
    public void clearRoutes() {
        routesBuilder = builder.routes();
    }

    /**
     * Add a new route. After adding all routes call 'buildRoutes'.
     */
    @NonNull
    public RefreshableRoutesLocator addRoute(@NonNull final String id, @NonNull final String path, @NonNull final URI uri) throws URISyntaxException {
        if (isNullOrEmpty(uri.getScheme())) {
            throw new URISyntaxException("Missing scheme in URI: {}", uri.toString());
        }

        routesBuilder.route(id, fn -> fn
                .path(path + "/**")
                .filters(filterSpec -> setupRouteFilters(path, uri, filterSpec))
                .uri(uri)
        );

        return this;
    }

    @NonNull
    private UriSpec setupRouteFilters(@NonNull final String path, @NonNull final URI uri, @NonNull GatewayFilterSpec filterSpec) {
        filterSpec.stripPrefix(1);

        // setup the retry filter, it is important as during transitions from one page to another access problems can occur.
        filterSpec.retry(config -> {
            config.setRetries(5);
            config.setStatuses(HttpStatus.INTERNAL_SERVER_ERROR);
            config.setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE);
        });

        String prefixPath = uri.getPath();
        if (!isNullOrEmpty(prefixPath)) {
            filterSpec.setPath(prefixPath);
        }

        // handle redirects coming from a routed service
        //  the service may be aware of request header 'X-Forwarded-Prefix'
        filterSpec.addRequestHeader("X-Forwarded-Prefix", path);
        //  as a fallback for services not aware of 'X-Forwarded-Prefix' we correct the Location header in response
        filterSpec.filter(new ModifyResponseHeaderLocationGatewayFilterFactory(lifeCycle).apply(c -> {
            System.out.println("xxxxxxxxxxx - test");
            c.setName(path + "/");
        }));

        return filterSpec;
    }

    /**
     * Call this method in order to publish all routes defined by 'addRoute' calls.
     */
    public void buildRoutes() {
        this.route = routesBuilder.build().getRoutes();
        gatewayRoutesRefresher.refreshRoutes();
    }

    public void deleteRoute(String id) {
        clearRoutes();
        gatewayRoutesRefresher.refreshRoutes();
    }


    public static boolean isNullOrEmpty(String param) {
        return param == null || param.trim().length() == 0;
    }


    @Override
    public Flux<Route> getRoutes() {
        return route;
    }

    public void setLifeCycle(ApplicationLifeCycleManager lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public ApplicationLifeCycleManager getLifeCycle() {
        return lifeCycle;
    }
}