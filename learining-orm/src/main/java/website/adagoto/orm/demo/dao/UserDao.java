package website.adagoto.orm.demo.dao;

import website.adagoto.orm.demo.pojo.User;

import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.demo
 * @Description: 用户dao的测试
 * @date Date : 2021年07月11日 20:43
 */
public interface UserDao {
    /**
     * 添加一个用户的样例
     *
     * @param user
     */
    public Integer addUser(User user);

    /**
     * 更新一个用户
     *
     * @param user
     * @return
     */
    public Integer updateUser(User user);

    /**
     * 通过ID删除用户
     *
     * @param id
     * @return
     */
    public Integer deleteById(String id);

    /**
     * 条件查询
     *
     * @param id
     * @return
     */
    public User queryById(String id);

    /**
     * 通过名称查询列表
     *
     * @param name
     * @return
     */
    public List<User> queryListByName(String name);
}
