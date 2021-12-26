package website.adagoto.demo.controller;

import website.adagoto.demo.pojo.User;
import website.adagoto.demo.service.QueryService;
import website.adagoto.framework.annotation.Autowired;
import website.adagoto.framework.annotation.Controller;
import website.adagoto.framework.annotation.RequestMapping;
import website.adagoto.framework.annotation.RequestParam;
import website.adagoto.framework.support.CommonUtil;
import website.adagoto.framework.webmvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.demo.controller
 * @Description: TODO
 * @date Date : 2021年07月03日 14:43
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private QueryService queryService;

    @RequestMapping("/query")
    public ModelAndView query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name) throws IOException {
        List<User> result = queryService.query(name);
        if (CommonUtil.isEmpty(result))
        {
            return null;
        }
        User user = result.get(0);
        Map<String, String> map = new HashMap<>();
        map.put("id",user.getId());
        map.put("name",user.getName());
        map.put("password",user.getPassword());
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        mv.setModel(map);
        return mv;
    }
}
