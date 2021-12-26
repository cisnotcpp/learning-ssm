package website.adagoto.demo.service.impl;

import website.adagoto.demo.dao.UserDao;
import website.adagoto.demo.pojo.User;
import website.adagoto.demo.service.QueryService;
import website.adagoto.framework.annotation.Autowired;
import website.adagoto.framework.annotation.Service;

import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.demo.service.impl
 * @Description: TODO
 * @date Date : 2021年07月03日 14:39
 */
@Service
public class QueryServiceImpl implements QueryService {
    @Autowired
    private UserDao userDao;

    @Override
    public List<User> query(String name) {
        return userDao.query(name);
    }
}
