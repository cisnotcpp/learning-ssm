package website.adagoto.orm.datasource.pooled;

import website.adagoto.orm.configuration.Configuration;
import website.adagoto.orm.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource.pooled
 * @Description: 缓冲的数据连接池
 * @date Date : 2021年07月11日 17:23
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    public PooledDataSourceFactory() {
        this.dataSource = new PooledDataSource();
    }

    public PooledDataSourceFactory(Configuration configuration) {
        this.dataSource = new PooledDataSource(configuration.getDriver(), configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
    }

    public PooledDataSourceFactory(String driver, String url, String username, String password) {
        this.dataSource = new PooledDataSource(driver, url, username, password);
    }
}
