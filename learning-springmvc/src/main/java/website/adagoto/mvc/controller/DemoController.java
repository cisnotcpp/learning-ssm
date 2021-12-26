package website.adagoto.mvc.controller;

import website.adagoto.mvc.annotation.Autowired;
import website.adagoto.mvc.annotation.Controller;
import website.adagoto.mvc.annotation.RequestMapping;
import website.adagoto.mvc.annotation.RequestParam;
import website.adagoto.mvc.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc.annotation.website.adagoto.mvc.controller
 * @Description: TODO
 * @date Date : 2021年06月20日 10:39
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    private UserService userService;

    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name) {
        String result = userService.getUserName(name);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/queryTest/[A-Za-z0-9]*")
    public String queryTest(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name) {
        return userService.getUserName(name);

    }
}
