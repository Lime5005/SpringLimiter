# SpringLimiter
Learn SpringBoot traffic limiter
- With basic guava limiter, try refresh "http://localhost:8080/limit/test1" constantly, get the message "Too crowd, please try again later".
- With AOP self-defined annotation, try refresh "http://localhost:8080/limit/test2" and "http://localhost:8080/limit/test3" constantly, get messages according to the respective key.
