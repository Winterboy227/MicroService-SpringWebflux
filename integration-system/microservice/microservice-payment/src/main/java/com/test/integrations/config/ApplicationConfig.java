package com.test.integrations.config;


import com.test.integrations.domain.seb.SebPayments;
import com.test.integrations.domain.seb.SebService;
import com.test.integrations.domain.seb.impl.SebPaymentFlowService;
import com.test.integrations.infrastructure.SebRemoteService;
import com.test.integrations.lib.domain.ISebReactiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Import({
        IntegrationConfig.class,
        MockedConfig.class
})
@Configuration
public class ApplicationConfig  {
    public static String NAME;
    @Autowired
    void setAppName(@Value("${spring.application.name:}") final String appname) {
        NAME = appname;
    }
    @Bean
    public SebPayments sebPaymentsService(final SebService sebService) {
        return new SebPaymentFlowService(sebService);
    }
    @Bean
    public SebService sebStatusService(final ISebReactiveService sebReactiveService){
        return new SebRemoteService(sebReactiveService);
    }


}
