package website.adagoto.demo.dao;

import website.adagoto.demo.pojo.User;

import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.demo.dao
 * @Description: TODO
 * @date Date : 2021年07月03日 18:27
 */
public interface UserDao {
    public List<User> query(String name);
}
