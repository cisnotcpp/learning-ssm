package website.adagoto.framework.webmvc;

import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.webmvc
 * @Description: 存储页面的位置和信息
 * @date Date : 2021年06月29日 19:50
 */
public class ModelAndView {

    private String viewName;

    private Map<String, ?> model;

    public ModelAndView() {
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
