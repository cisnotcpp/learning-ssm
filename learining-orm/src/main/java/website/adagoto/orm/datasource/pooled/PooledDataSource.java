package website.adagoto.orm.datasource.pooled;

import website.adagoto.orm.datasource.unpooled.UnpooledDataSource;
import website.adagoto.orm.util.CloseableUtil;
import website.adagoto.orm.util.ValidUtil;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: orm
 * @Package website.adagoto.orm.datasource.pooled
 * @Description: 连接池
 * @date Date : 2021年07月10日 16:00
 */
public class PooledDataSource implements DataSource {
    /**
     * 描述自己的状态
     */
    private PooledState pooledState = new PooledState(this);

    private UnpooledDataSource dataSource;
    /**
     * 最大工作激活连接数量
     */
    private int poolMaximumActiveConnections = 10;
    /**
     * 最大空闲连接
     */
    private int poolMaximumIdleConnections = 5;

    public PooledDataSource() {
        dataSource = new UnpooledDataSource();
    }

    public PooledDataSource(UnpooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PooledDataSource(String driver, String url, String username, String password) {
        this.dataSource = new UnpooledDataSource(driver, url, username, password);
    }

    public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
        this.dataSource = new UnpooledDataSource(driverClassLoader, driver, url, username, password);
    }

    public PooledDataSource(String driver, String url, Properties driverProperties) {
        this.dataSource = new UnpooledDataSource(driver, url, driverProperties);
    }

    public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        this.dataSource = new UnpooledDataSource(driverClassLoader, driver, url, driverProperties);
    }

    /**
     * 释放一个连接
     *
     * @param connection
     */
    public void pushConnection(PooledConnection connection) throws SQLException {
        synchronized (pooledState) {
            //移除
            pooledState.activeConnections.remove(connection);
            if (!connection.getRealConnection().getAutoCommit()) {
                connection.getRealConnection().rollback();
            }
            if (pooledState.idleConnections.size() < poolMaximumIdleConnections) {
                PooledConnection newConnection = new PooledConnection(this, connection.getRealConnection());
                pooledState.idleConnections.add(newConnection);
                pooledState.notifyAll();
            } else {
                //满了直接关闭
                CloseableUtil.close(connection.getRealConnection());
            }
        }
    }

    /**
     * 弹出一个连接
     *
     * @param username
     * @param password
     * @return
     */
    public PooledConnection popConnection(String username, String password) throws SQLException {
        PooledConnection connection = null;
        //从空闲的队列里面获取
        while (ValidUtil.isEmpty(connection)) {
            synchronized (pooledState) {
                if (!ValidUtil.isEmpty(pooledState.idleConnections)) {
                    connection = pooledState.idleConnections.remove(0);
                } else {
                    if (pooledState.activeConnections.size() < poolMaximumActiveConnections) {
                        connection = new PooledConnection(this, dataSource.getConnection());
                    }
                }
                if (!ValidUtil.isEmpty(connection)) {
                    pooledState.activeConnections.add(connection);
                }
            }
        }
        if (ValidUtil.isEmpty(connection)) {
            throw new SQLException("获取连接失败");
        }
        return connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    @Override
    public <T> T unwrap(Class<T> face) throws SQLException {
        throw new SQLException(getClass().getName() + " 不是包装");
    }

    @Override
    public boolean isWrapperFor(Class<?> face) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }

    /**
     * 强制关闭所有连接
     */
    private void forceCloseAll() throws SQLException {
        //关闭空闲的
        closePooledConnections(pooledState.idleConnections);
        //关闭正在激活的连接
        closePooledConnections(pooledState.activeConnections);
    }

    /**
     * 关闭连接
     *
     * @param pooledConnections
     */
    private void closePooledConnections(List<PooledConnection> pooledConnections) {
        Iterator<PooledConnection> iterator = pooledConnections.iterator();
        while (iterator.hasNext()) {
            try {
                PooledConnection pooledConnection = iterator.next();
                Connection realConnection = pooledConnection.getRealConnection();
                if (!realConnection.getAutoCommit()) {
                    realConnection.rollback();
                }
                CloseableUtil.close(realConnection);
            } catch (SQLException se) {
            } finally {
                //移除
                iterator.remove();
            }
        }
    }

    /**
     * 拆装连接，获取真实的连接
     *
     * @param connection
     * @return
     */
    public static Connection unwrapConnection(Connection connection) {
        if (Proxy.isProxyClass(connection.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(connection);
            if (handler instanceof PooledConnection) {
                return ((PooledConnection) handler).getRealConnection();
            }
        }
        return connection;
    }
}
