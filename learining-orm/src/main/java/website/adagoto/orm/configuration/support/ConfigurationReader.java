package website.adagoto.orm.configuration.support;

import website.adagoto.orm.configuration.Configuration;
import website.adagoto.orm.util.CloseableUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.configuration.support
 * @Description: 配置文件读取器
 * @date Date : 2021年07月12日 20:58
 */
public class ConfigurationReader {

    private Configuration configuration;

    private Properties properties = new Properties();
    /**
     * 存放配置文件的地址
     */
    private List<String> mappers = new ArrayList<>();

    private String url = "datasource.url";

    private String driver = "datasource.driver";

    private String username = "datasource.username";

    private String password = "datasource.password";

    private String mapperLocation = "datasource.mapperLocation";

    public ConfigurationReader(String configLocation) {
        loadConfiguration(configLocation);
    }

    /**
     * 加载配置到内存
     *
     * @param configLocation
     */
    private void loadConfiguration(String configLocation) {
        InputStream is = null;
        try {
            URL configUrl = this.getClass().getClassLoader().getResource(configLocation.replace("classpath:", ""));
            is = new FileInputStream(configUrl.getFile());
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseableUtil.close(is);
        }
    }

    /**
     * 获取配置
     *
     * @return
     */
    public Configuration getConfiguration() {
        if (null == configuration) {
            configuration = new Configuration();
            configuration.setUrl(properties.getProperty(url));
            configuration.setDriver(properties.getProperty(driver));
            configuration.setUsername(properties.getProperty(username));
            configuration.setPassword(properties.getProperty(password));
            configuration.setMapperLocation(properties.getProperty(mapperLocation));
        }
        return configuration;
    }

    /**
     * 扫描mapper文件
     */
    public List<String> loadMappers() {
        String mapperRegex = getConfiguration().getMapperLocation().replace("classpath:", "");
        String rootDirectory = mapperRegex.split("/")[0];
        Pattern mapperPattern = Pattern.compile(mapperRegex.replaceAll("/", "\\/").replaceAll("\\*", ".*"));
        scanMapper(rootDirectory, mapperPattern);
        return mappers;
    }

    /**
     * 扫描mapper地址
     *
     * @param directory
     * @param pattern
     */
    private void scanMapper(String directory, Pattern pattern) {
        URL mapperUrl = this.getClass().getClassLoader().getResource(directory);
        File dir = new File(mapperUrl.getFile());
        for (File f : dir.listFiles()) {
            String path = directory + "/" + f.getName();
            if (f.isDirectory()) {
                scanMapper(path, pattern);
            } else {
                if (!pattern.matcher(path).find()) {
                    continue;
                }
                this.mappers.add(path);
            }
        }
    }

    /**
     * 获取配置
     *
     * @return
     */
    public Properties getProperties() {
        return properties;
    }

}
