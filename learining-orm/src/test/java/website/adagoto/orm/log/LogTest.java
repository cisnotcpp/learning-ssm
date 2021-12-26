package website.adagoto.orm.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.log
 * @Description: TODO
 * @date Date : 2021年07月17日 17:13
 */

public class LogTest {
    private static final Logger logger = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void printLogTest() {
        logger.debug("测绘");
    }
}
