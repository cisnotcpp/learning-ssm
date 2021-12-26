package website.adagoto.framework.servlet;

import website.adagoto.framework.annotation.Controller;
import website.adagoto.framework.annotation.RequestMapping;
import website.adagoto.framework.context.ApplicationContext;
import website.adagoto.framework.support.CommonUtil;
import website.adagoto.framework.webmvc.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.servlet
 * @Description: 分发
 * @date Date : 2021年06月26日 15:36
 */
public class DispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();
    /**
     * 上下文
     */
    private ApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取处理器
     *
     * @param request
     * @return
     */
    private HandlerMapping getHandler(HttpServletRequest request) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        uri = uri.replace(contextPath, "").replace("/+", "/");
        for (HandlerMapping handler : this.handlerMappings) {
            if (handler.getPattern().matcher(uri).find()) {
                return handler;
            }
        }
        return null;
    }

    /**
     * 获取参数处理器
     *
     * @param handler
     * @return
     */
    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        HandlerAdapter handlerAdapter = this.handlerAdapters.get(handler);
        if (handlerAdapter.supports(handler)) {
            return handlerAdapter;
        }
        return null;
    }

    /**
     * @param request
     * @param response
     */
    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {
        if (null == mv) {
            return;
        }
        if (CommonUtil.isEmpty(this.viewResolvers)) {
            return;
        }
        for (ViewResolver resolver : this.viewResolvers) {
            View view = resolver.resolveViewName(mv.getViewName(), null);
            if (null != view) {
                view.render(mv.getModel(), request, response);
                return;
            }
        }
    }

    /**
     * 分发
     *
     * @param request
     * @param response
     * @throws Exception
     */
    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HandlerMapping handler = getHandler(request);
        if (null == handler) {
            processDispatchResult(request, response, new ModelAndView("404"));
            return;
        }
        HandlerAdapter handlerAdapter = getHandlerAdapter(handler);
        ModelAndView mv = handlerAdapter.handle(request, response, handler);
        processDispatchResult(request, response, mv);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        context = new ApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    /**
     * 初始化策略
     *
     * @param context
     */
    protected void initStrategies(ApplicationContext context) {
        // 初始化就打九大组件
        //上传组件
        initMultipartResolver(context);
        //本地化解析
        initLocaleResolver(context);
        //主题解析
        initThemeResolver(context);
        // 初始化请求处理解析器
        initHandlerMappings(context);
        //初始化参数处理器
        initHandlerAdapters(context);
        // 初始化异常处理器
        initHandlerExceptionResolvers(context);
        // 将请求转换到视图
        initRequestToViewNameTranslator(context);
        // 初始化试图解析器
        initViewResolvers(context);
        //初始化Flash管理器
        initFlashMapManager(context);
    }

    /**
     * Flash映射管理器
     *
     * @param context
     */
    private void initFlashMapManager(ApplicationContext context) {
    }

    /**
     * 初始化试图解析器
     *
     * @param context
     */
    private void initViewResolvers(ApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        if (templateRootDir.isDirectory()) {
            for (File template : templateRootDir.listFiles()) {
                this.viewResolvers.add(new ViewResolver(templateRoot));
            }
        }
    }

    /**
     * 初始化请求到试图名称的
     *
     * @param context
     */
    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }

    /**
     * 异常处理器
     *
     * @param context
     */
    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    /**
     * 初始化参数处理器
     *
     * @param context
     */
    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping mapping : this.handlerMappings) {
            this.handlerAdapters.put(mapping, new HandlerAdapter());
        }
    }

    /**
     * 初始化映射处理器
     *
     * @param context
     */
    private void initHandlerMappings(ApplicationContext context) {
        //已经扫到的beanName
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                //获取控制器
                Object controller = context.getBean(beanName);
                if (null == controller){
                    continue;
                }
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(Controller.class)) {
                    continue;
                }
                StringBuilder beanUrlBuilder = new StringBuilder("/");
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                    beanUrlBuilder.append(annotation.value());
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {
                        continue;
                    }
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    beanUrlBuilder.append("/").append(requestMapping.value());
                    String regex = beanUrlBuilder.toString().replaceAll("/+", "/").replaceAll("\\*", ".*");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new HandlerMapping(controller, method, pattern));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化主题
     *
     * @param context
     */
    private void initThemeResolver(ApplicationContext context) {
    }

    /**
     * 本地化解析
     *
     * @param context
     */
    private void initLocaleResolver(ApplicationContext context) {
    }

    /**
     * 初始化上传组件
     *
     * @param context
     */
    private void initMultipartResolver(ApplicationContext context) {
    }
}
