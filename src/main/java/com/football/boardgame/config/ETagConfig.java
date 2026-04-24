package com.football.boardgame.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/**
 * Enables automatic ETag generation for all REST responses.
 * Clients can send If-None-Match to receive 304 Not Modified when data hasn't changed,
 * saving bandwidth and improving mobile offline-first performance.
 */
@Configuration
public class ETagConfig {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterBean =
                new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filterBean.addUrlPatterns("/api/*");
        filterBean.setName("etagFilter");
        return filterBean;
    }
}
