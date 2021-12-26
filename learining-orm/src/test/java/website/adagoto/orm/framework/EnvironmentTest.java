package website.adagoto.orm.framework;

import org.junit.Test;
import website.adagoto.orm.configuration.Environment;
import website.adagoto.orm.demo.dao.UserDao;
import website.adagoto.orm.demo.pojo.User;
import website.adagoto.orm.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource.unpooled
 * @Description: TODO
 * @date Date : 2021年07月17日 14:57
 */
public class EnvironmentTest {
    static final String configLocation = "classpath:database.properties";

    @Test
    public void environmentTest() {
        Environment environment = new Environment(configLocation);
    }

    @Test
    public void sqlSessionTest() {
        SqlSession sqlSession = new SqlSession(configLocation);
        UserDao userDao = (UserDao) sqlSession.getObject(UserDao.class);
        User user = new User();
        user.setId("121221");
        user.setName("21");
        user.setPassword("123456");
        System.out.println(userDao.addUser(user));
    }

    @Test
    public void deleteTest() {
        SqlSession sqlSession = new SqlSession(configLocation);
        UserDao userDao = (UserDao) sqlSession.getObject(UserDao.class);
        User user = new User();
        user.setId("1");
        user.setName("liguiyang");
        user.setPassword("1234516");
        System.out.println(userDao.updateUser(user));
    }

    @Test
    public void queryByIdTest() {
        SqlSession sqlSession = new SqlSession(configLocation);
        UserDao userDao = (UserDao) sqlSession.getObject(UserDao.class);
        System.out.println(userDao.queryById("1"));
    }

    @Test
    public void queryListTest() {
        SqlSession sqlSession = new SqlSession(configLocation);
        UserDao userDao = (UserDao) sqlSession.getObject(UserDao.class);
        List<User> list = userDao.queryListByName("ui");
        for (User user : list) {
            System.out.println(user);
        }
    }

}
