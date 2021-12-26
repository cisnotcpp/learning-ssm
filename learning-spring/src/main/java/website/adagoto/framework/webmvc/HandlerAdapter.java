package website.adagoto.framework.webmvc;

import website.adagoto.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.webmvc
 * @Description: 处理方法级别的参数
 * @date Date : 2021年06月29日 19:48
 */
public class HandlerAdapter {
    /**
     * 是否支持
     * @param handler
     * @return
     */
    public boolean supports(Object handler){
        return handler instanceof HandlerMapping;
    }

    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        HandlerMapping handlerMapping = (HandlerMapping) handler;
        //形参列表
        Map<String, Integer> paramMapping = new HashMap<>();

        Annotation[][] parameterAnnotations = handlerMapping.getMethod().getParameterAnnotations();

        for (int i = 0 ;i  < parameterAnnotations.length; i++){
            for (Annotation a : parameterAnnotations[i]){
                if (a instanceof RequestParam){
                    String paramName = ((RequestParam) a).value();
                    if (!"".equals(paramName)){
                        paramMapping.put(paramName, i);
                    }
                }
            }
        }

        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        /**
         * 提取Request,Response
         */
        for (int i = 0 ; i < parameterTypes.length; i++){
            Class<?> type = parameterTypes[i];
            if (type == HttpServletResponse.class || type == HttpServletRequest.class){
                paramMapping.put(type.getName(), i);
            }
        }

        Map<String, String[]> parameterMap = request.getParameterMap();
        /**
         * 构建实参列表
         */
        Object[] paramValues = new Object[parameterTypes.length];

        for (Map.Entry<String, String[]> param : parameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");
            if (!paramMapping.containsKey(param.getKey())){
                continue;
            }
            int index = paramMapping.get(param.getKey());
            paramValues[index] = value;
        }

        if (paramMapping.containsKey(HttpServletRequest.class.getName())){
            int requestIndex = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[requestIndex] = request;
        }

        if (paramMapping.containsKey(HttpServletResponse.class.getName())){
            int responseIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[responseIndex] = response;
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (null == result){
            return null;
        }
        return handlerMapping.getMethod().getReturnType() == ModelAndView.class ? (ModelAndView) result : null;
    }
}
