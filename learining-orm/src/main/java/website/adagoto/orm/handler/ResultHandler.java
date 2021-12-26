package website.adagoto.orm.handler;

import website.adagoto.orm.exception.IllegalException;
import website.adagoto.orm.mapping.ClassMapping;
import website.adagoto.orm.util.ValidUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.handler
 * @Description: 类型处理器，处理放回结果的
 * @date Date : 2021年07月17日 20:11
 */
public class ResultHandler<T> implements Serializable {
    /**
     * 方法的返回参数
     */
    private Class<?> methodReturnType;
    /**
     * 泛型的实际类型
     */
    private ParameterizedType methodGenericReturnType;
    /**
     * mapper方法中配置的参数
     */
    private Class<?> mapperReturnType;
    /**
     * 结果
     */
    private T data;

    public ResultHandler(Class<?> methodReturnType, Type type, Class<?> mapperReturnType, T data) {
        this.data = data;
        this.methodReturnType = methodReturnType;
        this.mapperReturnType = mapperReturnType;
        this.methodGenericReturnType = (ParameterizedType) type;
    }

    /**
     * 包装
     *
     * @return
     */
    public Object wrap() throws IllegalException {
        //返回值为void
        if (methodReturnType == void.class || methodReturnType == Void.class) {
            return null;
        }
        /**
         * 更新只有条数
         */
        if (isModifyOperation(methodReturnType) && isModifyOperation(data.getClass())) {
            if (!ValidUtil.isNull(mapperReturnType) && !isModifyOperation(mapperReturnType)) {
                throw new IllegalException("配置异常");
            }
            return data;
        }
        if (data.getClass() != Integer.class && data.getClass().isAssignableFrom(List.class)) {
            throw new IllegalException("范围结果异常");
        }
        return wrapObject();
    }

    /**
     * 是否是更新操作
     *
     * @param clazz
     * @return
     */
    private boolean isModifyOperation(Class<?> clazz) {
        return clazz == int.class || clazz == Integer.class;
    }

    /**
     * 包装对象
     *
     * @return
     */
    private Object wrapObject() throws IllegalException {
        if (isModifyOperation(methodReturnType)) {
            throw new IllegalException("方法返回参数错误");
        }
        if (methodReturnType.isAssignableFrom(List.class)) {//返回的是List
            return wrapList();
        } else if (methodReturnType.isAssignableFrom(Map.class)) {//返回的是Map
            return wrapMap();
        } else if (ClassMapping.isBaseType(methodReturnType)) {//返回的是一个基本类型
            return wrapBaseType();
        } else {//返回的是一个对象
            return wrapCommonObject();
        }
    }

    /**
     * 包装基本类型
     *
     * @return
     */
    private Object wrapBaseType() throws IllegalException {
        List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) data;
        return ValidUtil.isEmpty(resultMapList) ? null : resultMapList.get(0).values().toArray()[0];
    }

    /**
     * 包装一个map
     *
     * @return
     */
    private Object wrapMap() throws IllegalException {
        List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) data;
        return ValidUtil.isEmpty(resultMapList) ? null : resultMapList.get(0);
    }

    /**
     * 包装list
     *
     * @return
     * @throws IllegalException
     */
    private Object wrapList() throws IllegalException {
        if (!ValidUtil.isNull(mapperReturnType) && !ValidUtil.isNull(methodGenericReturnType)) {
            Type[] typeArguments = methodGenericReturnType.getActualTypeArguments();
            Class<?> typeArgClass = (Class<?>) typeArguments[0];
            if (mapperReturnType != typeArgClass) {
                throw new IllegalException("返回类型和配置的返回类型不一致");
            }
        }
        List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) data;
        List<Object> resultList = new ArrayList<>();
        Field[] fields = mapperReturnType.getDeclaredFields();
        for (Map<String, Object> map : resultMapList) {
            try {
                Object o = mapperReturnType.newInstance();
                //初始化字段
                for (Field f : fields) {
                    f.setAccessible(true);
                    f.set(o, map.get(mapCamelCaseToUnderscore(f.getName())));
                }
                resultList.add(o);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * 包装一个对象
     *
     * @return
     */
    private Object wrapCommonObject() throws IllegalException {
        if (methodReturnType != mapperReturnType) {
            throw new IllegalException("返回类型和配置类型不一致");
        }
        List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) data;
        if (ValidUtil.isEmpty(resultMapList)) {
            return null;
        }
        try {
            Object resultObject = mapperReturnType.newInstance();
            Map<String, Object> objectMap = resultMapList.get(0);
            Field[] fields = mapperReturnType.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                f.set(resultObject, objectMap.get(mapCamelCaseToUnderscore(f.getName())));
            }
            return resultObject;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 驼峰映射
     *
     * @param string
     * @return
     */
    public static String mapUnderscoreToCamelCase(String string) {
        string = string.toLowerCase();
        final StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("_(\\w)");
        Matcher m = p.matcher(string);
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将字段映射成数据库字段
     *
     * @param field
     * @return
     */
    public static String mapCamelCaseToUnderscore(String field) {
        final StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("[A-Z]+");
        Matcher m = p.matcher(field);
        while (m.find()) {
            m.appendReplacement(sb, "_" + m.group(0).toLowerCase());
        }
        m.appendTail(sb);
        return sb.toString().toUpperCase(Locale.ENGLISH);
    }

    /**
     * 获取泛型的类型
     *
     * @param clazz
     * @param index
     * @return
     */
    private static Class<?> getGenericClass(Class<?> clazz, int index) {
        ParameterizedType superclass = (ParameterizedType) clazz.getTypeParameters()[0];
        return superclass.getActualTypeArguments()[index].getClass();
    }
}
