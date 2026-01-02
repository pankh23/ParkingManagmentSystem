package com.apc.parking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
public class PropertyConfig {

    /**
     * This ensures that environment variables are available as properties
     * for use in XML configuration files (applicationContext.xml)
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(Environment environment) {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        // This allows environment variables to override properties
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }
}

