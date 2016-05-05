package com.minhdtb.storm.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;

@Aspect
public class ErrorHandler {

    @AfterThrowing(pointcut = "execution(* *(..))", throwing = "e")
    public void throwing(JoinPoint joinPoint, Throwable e) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String arguments = Arrays.toString(joinPoint.getArgs());
        System.out.println("We have caught exception in method: "
                + methodName + " with arguments "
                + arguments + "\nException: "
                + e.getMessage());
    }
}
