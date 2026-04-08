package com.quadra.system.adapter.in.web.aspect;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    String module() default "";
    String action() default "";
}
