package website.adagoto.orm.proxy;

import website.adagoto.orm.handler.MethodHandler;
import website.adagoto.orm.util.ValidUtil;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.proxy
 * @Description: Mapper的代理
 * @date Date : 2021年07月17日 9:28
 */
public class MapperProxy<T> implements InvocationHandler {
    /**
     * 原始对象
     */
    private Class<T> mapper;
    /**
     * 上下文环境
     */
    private Map<Method, MethodHandler> handlerMap;
    /**
     * 数据连接池
     */
    private DataSource dataSource;

    public MapperProxy(Class<T> mapperClass, Map<Method, MethodHandler> handlerMap, DataSource dataSource) {
        mapper = mapperClass;
        this.handlerMap = handlerMap;
        this.dataSource = dataSource;
    }

    public T getProxy() {
        return (T) Proxy.newProxyInstance(mapper.getClassLoader(), new Class[]{mapper}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ValidUtil.isEmpty(handlerMap)) {
            return null;
        }
        MethodHandler methodHandler = handlerMap.get(method);
        if (ValidUtil.isNull(methodHandler)) {
            throw new NullPointerException(mapper.getName() + "中的" + method.getName() + "方法没有在配置文件中配置");
        }
        return methodHandler.handle(method, args, dataSource);
    }
}
