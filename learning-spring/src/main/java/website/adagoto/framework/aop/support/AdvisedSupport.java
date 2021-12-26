package website.adagoto.framework.aop.support;

import website.adagoto.framework.aop.AopConfig;
import website.adagoto.framework.aop.aspect.AfterThrowingAdvice;
import website.adagoto.framework.aop.aspect.MethodAfterAdvice;
import website.adagoto.framework.aop.aspect.MethodBeforeAdvice;
import website.adagoto.framework.support.CommonUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.support
 * @Description: 解析和封装AOP配置
 * @date Date : 2021年07月04日 10:55
 */
public class AdvisedSupport {
    private Class targetClass;
    private Object target;
    private Pattern pointCutClassPattern;
    private transient Map<Method, List<Object>> methodCache;

    private AopConfig config;

    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        this.parse();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (null == cached) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    private void parse() {
        String pointCut = config.getPointCut();
        pointCut = pointCut.replaceAll("\\.", "\\\\.");
        pointCut = pointCut.replaceAll("\\\\.\\*", ".*");
        pointCut = pointCut.replaceAll("\\(", "\\\\(");
        pointCut = pointCut.replaceAll("\\)", "\\\\)");
        pointCut.substring(0, pointCut.lastIndexOf("\\("));

        String pointCutForClass = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        //类的正则表达式
        pointCutClassPattern = Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") + 1));
        methodCache = new HashMap<>();
        Pattern pattern = Pattern.compile(pointCut);
        try {
            Class<?> aspectClass = Class.forName(config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }

            for (Method m : targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    //能满足切面规则的存入方法
                    List<Object> advices = new LinkedList<>();
                    if (!CommonUtil.isEmpty(config.getAspectBefore())) {
                        //添加前置通知
                        advices.add(new MethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    if (!CommonUtil.isEmpty(config.getAspectAfter())) {
                        //添加后置通知
                        advices.add(new MethodAfterAdvice(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }
                    if (!CommonUtil.isEmpty(config.getAspectAfterThrow())) {
                        //添加异常通知
                        AfterThrowingAdvice afterThrowingAdvice = new AfterThrowingAdvice(aspectMethods.get(config.getAspectAfterThrow()), aspectClass.newInstance());
                        afterThrowingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(afterThrowingAdvice);
                    }
                    //添加到通知
                    methodCache.put(m, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
