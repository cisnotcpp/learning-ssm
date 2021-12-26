package website.adagoto.mvc.service.impl;

import website.adagoto.mvc.annotation.Service;
import website.adagoto.mvc.service.UserService;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc.service.impl
 * @Description: TODO
 * @date Date : 2021年06月20日 10:41
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public String getUserName(String name) {
        return "UserServiceImpl处理了 ： " + name;
    }
}
