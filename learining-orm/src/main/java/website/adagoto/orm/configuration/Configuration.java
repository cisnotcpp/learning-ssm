package website.adagoto.orm.configuration;

import lombok.Data;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.configuration
 * @Description: 配置数据
 * @date Date : 2021年07月12日 20:59
 */
@Data
public class Configuration {
    /**
     * JDBC的url
     */
    private String url;
    /**
     * JDBC的驱动
     */
    private String driver;
    /**
     * 数据库用户名称
     */
    private String username;
    /**
     * 用户名的密码
     */
    private String password;
    /**
     * mapper文件路径
     */
    private String mapperLocation;
}
