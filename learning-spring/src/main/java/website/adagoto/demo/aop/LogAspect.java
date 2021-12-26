package website.adagoto.demo.aop;

import website.adagoto.framework.aop.aspect.JoinPoint;

import java.util.Arrays;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.demo.aop
 * @Description: TODO
 * @date Date : 2021年07月04日 10:39
 */
public class LogAspect {

    public void before(JoinPoint joinPoint) {
        System.out.println("invoke before method ! Target Object : " + joinPoint.getThis() + " Args : " + Arrays.toString(joinPoint.getArguments()));
    }

    public void after(JoinPoint joinPoint) {
        System.out.println("invoke after method ! Target Object : " + joinPoint.getThis() + " Args : " + Arrays.toString(joinPoint.getArguments()));
    }

    public void afterThrowing(JoinPoint joinPoint, Throwable throwable) {
        System.out.println("出现异常 ! Target Object : " + joinPoint.getThis() + " Args : " + Arrays.toString(joinPoint.getArguments()) + "  \t Throws : " + throwable.getMessage());
    }
}
