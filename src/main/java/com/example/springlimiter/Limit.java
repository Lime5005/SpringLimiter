package com.example.springlimiter;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Limit {
    String key() default ""; //Different key, different control.
    double permitsPerSecond(); //Request limits.
    long timeout(); //The maximum time to wait.
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;
    String msg() default "The server is busy, please try again later.";
}
