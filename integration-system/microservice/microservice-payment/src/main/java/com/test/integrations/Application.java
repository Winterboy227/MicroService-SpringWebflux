package com.test.integrations;

import com.test.integrations.config.ApplicationConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import static org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

@Import(ApplicationConfig.class)
@SpringBootApplication
@ComponentScan(basePackages = "com.test.integrations",
    excludeFilters = {
        @Filter(type = FilterType.ANNOTATION, value = Configuration.class)})
public class Application {
    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class)
                .initializers((GenericApplicationContext c) -> c.setAllowBeanDefinitionOverriding(false))
                .build()
                .run(args);
    }
}
