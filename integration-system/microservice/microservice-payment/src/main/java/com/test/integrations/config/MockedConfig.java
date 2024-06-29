package com.test.integrations.config;

import com.test.integrations.annotaions.IfShouldMockIntegrations;
import com.test.integrations.lib.config.SebConfiguration;
import com.test.integrations.lib.domain.ISebReactiveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@IfShouldMockIntegrations
public class MockedConfig {
    @Bean
    public ISebReactiveService sebRestService() { return SebConfiguration.sebMockedService();}

}
