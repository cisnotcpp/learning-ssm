package website.adagoto.orm.datasource.unpooled;

import lombok.Data;
import website.adagoto.orm.util.ValidUtil;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource.pooled
 * @Description: 未集中的连接池
 * @date Date : 2021年07月11日 14:49
 */
@Data
public class UnpooledDataSource implements DataSource {
    /**
     * 驱动加载器
     */
    private ClassLoader driverClassLoader;
    private Properties driverProperties;
    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();
    private String url;
    private String driver;
    private String username;
    private String password;
    /**
     * 事物
     */
    private boolean autoCommit;
    /**
     * 事物隔离等级（有四个）
     */
    private Integer defaultTransactionIsolationLevel;
    /**
     * 默认网络超时
     */
    private Integer defaultNetworkTimeout;

    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            registeredDrivers.put(d.getClass().getName(), d);
        }
    }

    public UnpooledDataSource() {
    }

    public UnpooledDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
        this(driver, url, username, password);
        this.driverClassLoader = driverClassLoader;
    }

    public UnpooledDataSource(String driver, String url, Properties driverProperties) {
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        this(driver, url, driverProperties);
        this.driverClassLoader = driverClassLoader;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(this.username, this.password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " 不是包装类 ");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
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

    /**
     * 获取连接
     *
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties properties = new Properties();
        if (!ValidUtil.isEmpty(driverProperties)) {
            properties.putAll(driverProperties);
        }
        if (!ValidUtil.isEmpty(username)) {
            properties.put("user", username);
        }
        if (!ValidUtil.isEmpty(password)) {
            properties.put("password", password);
        }
        return doGetConnection(properties);
    }

    /**
     * 获取连接
     *
     * @param properties
     * @return
     * @throws SQLException
     */
    private Connection doGetConnection(Properties properties) throws SQLException {
        initializeDriver();
        Connection connection = DriverManager.getConnection(url, properties);
        configureConnection(connection);
        return connection;
    }

    /**
     * 配置连接
     *
     * @param connection
     */
    private void configureConnection(Connection connection) throws SQLException {
        //是否开始事物
        if (!ValidUtil.isEmpty(autoCommit)) {
            connection.setAutoCommit(autoCommit);
        }
        if (!ValidUtil.isEmpty(defaultNetworkTimeout)) {
            connection.setNetworkTimeout(Executors.newSingleThreadExecutor(), defaultNetworkTimeout);
        }
        //事物隔离等级
        if (!ValidUtil.isEmpty(defaultTransactionIsolationLevel)) {
            connection.setTransactionIsolation(defaultTransactionIsolationLevel);
        }
    }

    /**
     * 实例化驱动
     *
     * @throws SQLException
     */
    private void initializeDriver() throws SQLException {
        if (!registeredDrivers.containsKey(driver)) {
            try {
                Class<?> driverType = ValidUtil.isEmpty(driverClassLoader) ? Class.forName(driver) : Class.forName(driver, true, driverClassLoader);
                Driver driverInstance = (Driver) driverType.getDeclaredConstructor().newInstance();
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                registeredDrivers.put(driver, driverInstance);
            } catch (Exception e) {
                throw new SQLException("初始化驱动错误");
            }
        }
    }

    /**
     * 驱动代理
     */
    private class DriverProxy implements Driver {

        private Driver driver;

        public DriverProxy(Driver d) {
            this.driver = d;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return this.driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return this.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return this.driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return this.driver.getParentLogger();
        }
    }
}
