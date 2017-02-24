package com.nexon.apiserver.aspect;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class LoggingAdvice {
	
	private Logger logger = Logger.getLogger(LoggingAdvice.class);
	
	@Before("execution(* com.nexon.apiserver.handler.*Handler.*(..))")
	public void logMethodAndUri(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		HttpServletRequest request = (HttpServletRequest) args[args.length - 1];
		logger.info(":::::::::::::::: Request Method : " + request.getMethod() + " :::::::::::::::: URI : " + request.getServletPath() + " ::::::::::::::::");
	}
	
	@After("execution(* com.nexon.apiserver.handler.*.*(..))")
	public void logAfterMethod(JoinPoint joinPoint) {
		logger.info(":: Request Method End ::");
	}
}
