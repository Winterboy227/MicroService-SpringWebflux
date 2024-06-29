package com.test.integrations.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Profile("!local")
@Order(1)
public @interface IfShouldUseIntegrations {
}

