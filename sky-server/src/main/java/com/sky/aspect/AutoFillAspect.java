package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     *切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("公共字段填充...");

        //获取方法签名对象
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取方法上面的注解
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);

        //获取注解中的操作类型
        OperationType operationType = autoFill.value();
        //获取目标方法的所有参数
        Object[] args = joinPoint.getArgs();

        //判断参数是否为空
        if (args == null || args.length == 0) return;
        //获取第一个参数 其实只有一个也就是实体类
        Object object = args[0];
        //系统当前时间
        LocalDateTime time = LocalDateTime.now();
        //当前操作的员工id
        Long empId = BaseContext.getCurrentId();

        if (operationType == OperationType.INSERT){
            try {
                //获取对象方法
                Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射调用对象方法
                setCreateTime.invoke(object,time);
                setUpdateTime.invoke(object,time);
                setCreateUser.invoke(object,empId);
                setUpdateUser.invoke(object,empId);
            } catch (Exception ex) {
                log.error("公共字段自动填充失败：{}", ex.getMessage());
            }
        }else {
            try {
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            }catch (Exception ex){
                log.error("公共字段自动填充失败：{}", ex.getMessage());
            }
        }

    }
}

