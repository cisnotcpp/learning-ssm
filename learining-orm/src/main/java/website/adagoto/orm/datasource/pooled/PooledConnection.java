package website.adagoto.orm.datasource.pooled;

import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource.pooled
 * @Description: 描述连接的
 * @date Date : 2021年07月11日 15:54
 */
@Data
public class PooledConnection implements InvocationHandler {
    /**
     * 关闭连接的方法
     */
    private static final String CLOSE = "close";
    private static Class<?>[] IFACES = new Class<?>[]{Connection.class};
    /**
     * 真实的连接
     */
    private final Connection realConnection;
    /**
     * Jdk动态代理的一个连接
     */
    private final Connection proxyConnection;

    private final PooledDataSource dataSource;
    /**
     * 真实连接的hashCode
     */
    private final int hashCode;

    public PooledConnection(PooledDataSource dataSource, Connection connection) {
        this.dataSource = dataSource;
        this.realConnection = connection;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
        this.hashCode = connection.hashCode();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (CLOSE.equals(methodName)) {
            //如果关闭连接就放回去
            this.dataSource.pushConnection(this);
            return null;
        }
        return method.invoke(realConnection, args);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PooledConnection) {
            return realConnection.hashCode() == ((PooledConnection) o).realConnection.hashCode();
        } else if (o instanceof Connection) {
            return hashCode == o.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
