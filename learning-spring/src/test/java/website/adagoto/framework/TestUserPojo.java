package website.adagoto.framework;

import org.junit.Test;
import website.adagoto.framework.context.ApplicationContext;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework
 * @Description: TODO
 * @date Date : 2021年06月26日 17:51
 */
public class TestUserPojo {

    @Test
    public void testUserPojo(){
        try {
            ApplicationContext applicationContext = new ApplicationContext("classpath:application.properties");
            Object userPojo = applicationContext.getBean("userPojo");
            System.out.println(userPojo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
