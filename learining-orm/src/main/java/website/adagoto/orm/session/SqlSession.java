package website.adagoto.orm.session;

import website.adagoto.orm.configuration.Environment;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.session
 * @Description: sql的绘画
 * @date Date : 2021年07月17日 15:41
 */
public class SqlSession implements Closeable {
    /**
     * 环境
     */
    protected Environment environment;

    public SqlSession(String... configLocations) {
        this.environment = new Environment(configLocations[0]);
    }

    public Object getObject(Class<?> clazz) {
        return environment.getMapperMapping(clazz).getProxy();
    }

    @Override
    public void close() throws IOException {
        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
