package website.adagoto.orm.mapping;

import website.adagoto.orm.datasource.DataSourceFactory;
import website.adagoto.orm.handler.MethodHandler;
import website.adagoto.orm.proxy.MapperProxy;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.mapping
 * @Description: Mapper标签的映射
 * @date Date : 2021年07月12日 21:24
 */
public class MapperMapping<T> {
    /**
     * namespace的类
     */
    private Class<T> clazz;

    private MapperProxy<T> proxy;

    private Map<Method, MethodHandler> methodHandlerMap = new ConcurrentHashMap<>();

    public MapperMapping(Class<T> mapperClazz, DataSourceFactory dataSourceFactory) {
        this.clazz = mapperClazz;
        this.proxy = new MapperProxy<>(mapperClazz, methodHandlerMap, dataSourceFactory.getDataSource());
    }

    public MapperMapping(Class<T> mapperClazz, DataSource dataSource) {
        this.clazz = mapperClazz;
        this.proxy = new MapperProxy<>(mapperClazz, methodHandlerMap, dataSource);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * 添加映射
     *
     * @param method
     * @param handler
     */
    public void putMapping(Method method, MethodHandler handler) {
        methodHandlerMap.put(method, handler);
    }

    /**
     * 获取方法处理器
     *
     * @param method
     * @return
     */
    public MethodHandler getHandler(Method method) {
        return methodHandlerMap.get(method);
    }

    public T getProxy() {
        return this.proxy.getProxy();
    }
}
