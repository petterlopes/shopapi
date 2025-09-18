package com.peritumct.shopapi.config;

import com.peritumct.shopapi.domain.order.service.PriceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public PriceCalculator priceCalculator() {
        return new PriceCalculator();
    }
}
