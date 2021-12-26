package website.adagoto.orm.datasource.unpooled;

import org.junit.Test;
import website.adagoto.orm.datasource.pooled.PooledDataSource;
import website.adagoto.orm.util.CloseableUtil;

import java.sql.Connection;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource.unpooled
 * @Description: 测试数据连接池
 * @date Date : 2021年07月11日 15:28
 */
public class DataSourceTest {
    String url = "jdbc:mysql://localhost:3306/demo?&useSSL=false&serverTimezone=UTC";
    String driver = "com.mysql.cj.jdbc.Driver";
    String username = "root";
    String password = "331107";

    @Test
    public void getUnpooledConnection() {
        UnpooledDataSource dataSource = new UnpooledDataSource(driver, url, username, password);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            System.out.println(conn.isClosed());
        } catch (Exception se) {
            se.printStackTrace();
        }
        CloseableUtil.close(conn);
    }

    @Test
    public void getPooledConnection() {
        PooledDataSource dataSource = new PooledDataSource(driver, url, username, password);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            System.out.println(conn.isClosed());
        } catch (Exception se) {
            se.printStackTrace();
        }
        CloseableUtil.close(conn);
    }
}
