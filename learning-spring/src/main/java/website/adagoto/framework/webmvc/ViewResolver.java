package website.adagoto.framework.webmvc;

import website.adagoto.framework.support.CommonUtil;

import java.io.File;
import java.util.Locale;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.webmvc
 * @Description: 试图解析
 * @date Date : 2021年06月29日 20:47
 */
public class ViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    private String viewName;

    public ViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public View resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if (CommonUtil.isEmpty(viewName)) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : viewName + DEFAULT_TEMPLATE_SUFFIX;
        StringBuilder viewFilePathBuilder = new StringBuilder();
        viewFilePathBuilder.append(templateRootDir.getPath());
        viewFilePathBuilder.append("/");
        viewFilePathBuilder.append(viewName);
        File templateFile = new File(viewFilePathBuilder.toString().replaceAll("/+","/"));
        return new View(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
