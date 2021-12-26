package website.adagoto.framework.aop.intercept;

import website.adagoto.framework.aop.aspect.JoinPoint;
import website.adagoto.framework.support.CommonUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: TODO
 * @date Date : 2021年07月04日 10:48
 */
public class MethodInvocation implements JoinPoint {
    /**
     * 代理对象
     */
    private Object proxy;
    /**
     * 代理的目标方法
     */
    private Method method;
    /**
     * 代理的目标对象
     */
    private Object target;
    /**
     * 代理的目标类
     */
    private Class<?> targetClass;
    /**
     * 代理方法的实参列表
     */
    private Object[] arguments;
    /**
     * 回调方法链
     */
    private List<Object> interceptorsAndDynamicMethodMatches;
    /**
     * 保存自定义属性
     */
    private Map<String, Object> userAttributes;

    private int currentIntercptorIndex = -1;

    public MethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatches) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatches = interceptorsAndDynamicMethodMatches;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (!CommonUtil.isEmpty(value)) {
            if (CommonUtil.isEmpty(this.userAttributes)) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        } else {
            if (!CommonUtil.isEmpty(this.userAttributes)) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return CommonUtil.isEmpty(this.userAttributes) ? null : this.userAttributes.get(key);
    }

    public Object proceed() throws Throwable {
        if (this.currentIntercptorIndex == this.interceptorsAndDynamicMethodMatches.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatches.get(++this.currentIntercptorIndex);
        if (interceptorOrInterceptionAdvice instanceof MethodInterceptor) {
            MethodInterceptor mi = (MethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            return proceed();
        }
    }
}
