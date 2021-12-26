package website.adagoto.framework.aop.aspect;

import website.adagoto.framework.support.CommonUtil;

import java.lang.reflect.Method;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: TODO
 * @date Date : 2021年07月04日 11:29
 */
public abstract class AbstractAspectJAdvice implements Advice {
    /**
     * 织入的方法
     */
    private Method aspectMethod;
    /**
     * 织入类
     */
    private Object aspectTarget;

    public AbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable throwable) throws Throwable {
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if (CommonUtil.isEmpty(parameterTypes)) {
            //没有参数直接调用
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            //有参数
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = throwable;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(this.aspectTarget, args);
        }
    }
}
