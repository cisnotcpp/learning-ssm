package website.adagoto.orm.datasource.unpooled;

import website.adagoto.orm.configuration.Configuration;
import website.adagoto.orm.datasource.DataSourceFactory;

import javax.sql.DataSource;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource.unpooled
 * @Description: TODO
 * @date Date : 2021年07月11日 15:40
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {

    protected DataSource dataSource;

    public UnpooledDataSourceFactory() {
        this.dataSource = new UnpooledDataSource();
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    public UnpooledDataSourceFactory(Configuration configuration) {
        this.dataSource = new UnpooledDataSource(configuration.getDriver(), configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
    }

    public UnpooledDataSourceFactory(String driver, String url, String username, String password) {
        this.dataSource = new UnpooledDataSource(driver, url, username, password);
    }
}
