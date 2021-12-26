package website.adagoto.framework.aop.aspect;

import website.adagoto.framework.aop.intercept.MethodInterceptor;
import website.adagoto.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: TODO
 * @date Date : 2021年07月04日 12:28
 */
public class AfterReturningAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(joinPoint, returnValue, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        Object returnValue = mi.proceed();
        this.afterReturning(returnValue, mi.getMethod(), mi.getArguments(), mi.getThis());
        return returnValue;
    }
}
