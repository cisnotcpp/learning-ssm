package website.adagoto.mvc.dispatch;

import website.adagoto.mvc.annotation.*;
import website.adagoto.mvc.util.CheckUtil;
import website.adagoto.mvc.util.StreamUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc.dispatch
 * @Description: TODO
 * @date Date : 2021年06月20日 14:42
 */
public class MvcDispatchServlet3 extends HttpServlet {
    //存放配置文件
    private Properties configContext = new Properties();
    //扫描到的类名称,全限定名称
    private Set<String> classNameSet = new HashSet<>();
    // IOC容器
    private Map<String, Object> ioc = new HashMap<>();
    // url和方法映射map
    List<Handler> handlers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispath(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过正则获取方法
     *
     * @param request
     * @return
     */
    private Handler getHandler(HttpServletRequest request) {
        if (CheckUtil.isEmpty(handlers)) {
            return null;
        }
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (Handler handler : handlers) {
            try {
                Matcher matcher = handler.pattern.matcher(url);
                if (matcher.matches()) {
                    return handler;
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return null;
    }


    /**
     * 分发
     *
     * @param req
     * @param resp
     */
    private void doDispath(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        Handler handler = getHandler(req);
        if (CheckUtil.isEmpty(handler)) {
            resp.getWriter().write(" 404 NOT FOUND! ");
            return;
        }
        //获取方法的形参列表
        Object[] paramValues = new Object[handler.method.getParameterTypes().length];
        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String value = Arrays.toString(entry.getValue());
            if (handler.paramIndexMapping.containsKey(entry.getKey())) {
                int paramIndex = handler.paramIndexMapping.get(entry.getKey());
                paramValues[paramIndex] = value;
            }
        }
        if (handler.paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            paramValues[handler.paramIndexMapping.get(HttpServletRequest.class.getName())] = req;
        }
        if (handler.paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            paramValues[handler.paramIndexMapping.get(HttpServletResponse.class.getName())] = resp;
        }

        Object result = handler.method.invoke(handler.controller, paramValues);
        if (CheckUtil.isEmpty(result) || result instanceof Void){
            return ;
        }
        resp.getWriter().write(result.toString());
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2.扫描相关的类
        doScannerClass(configContext.getProperty("scanPackage"));
        //3.初始化实体类存入ioc中
        doInstances();
        //4.初始化扫描到的类，并存放到ioc中
        doAutowired();
        //5.初始化方法映射器
        doInitUrlMapping();
    }

    //初始化实例
    private void doInstances() {
        if (CheckUtil.isEmpty(classNameSet)) {
            return;
        }
        try {
            for (String className : classNameSet) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    ioc.put(toLowerFristCase(clazz.getSimpleName()), clazz.newInstance());
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();
                    if ("".equals(beanName.trim())) {
                        beanName = clazz.getSimpleName();
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(toLowerFristCase(beanName), instance);
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new IllegalAccessException("The " + beanName + " is exist!");
                        }
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception ce) {
            ce.printStackTrace();
        }
    }

    /**
     * 初始化url和method映射Map
     */
    private void doInitUrlMapping() {
        if (CheckUtil.isEmpty(ioc)) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                //保存requestMapping
                String baseUrl = "";
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                // 获取方法，建立方法和url的映射
                for (Method method : clazz.getMethods()) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {
                        continue;
                    }
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    StringBuilder methodUrlBuilder = new StringBuilder();
                    methodUrlBuilder.append(baseUrl);
                    methodUrlBuilder.append("/");
                    methodUrlBuilder.append(requestMapping.value());
                    String regex = methodUrlBuilder.toString().replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    handlers.add(new Handler(pattern, entry.getValue(), method));
                }
            }
        }
    }

    /**
     * 执行自动注入
     * 对IOC中的instance进行注入
     */
    private void doAutowired() {
        if (CheckUtil.isEmpty(ioc)) {
            return;
        }
        try {
            //给字段属性赋值
            for (Map.Entry<String, Object> entry : ioc.entrySet()) {
                Field[] fields = entry.getValue().getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(Autowired.class)) {
                        continue;
                    }
                    //从IOC容器中寻找对应的属性
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String beanName = autowired.value();
                    if (CheckUtil.isEmpty(beanName)) {
                        beanName = field.getType().getName();
                    }
                    if (!ioc.containsKey(beanName)) {
                        throw new IllegalAccessException(beanName + " 不存在");
                    }
                    //暴力访问
                    field.setAccessible(true);
                    field.set(entry.getValue(), ioc.get(beanName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //根据配置的包扫描类
    private void doScannerClass(String scanPackage) {
        URL packageUrl = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File baseDir = new File(packageUrl.getFile());
        for (File file : baseDir.listFiles()) {
            if (file.isDirectory()) {
                doScannerClass(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                StringBuilder classNameBuilder = new StringBuilder();
                classNameBuilder.append(scanPackage);
                classNameBuilder.append(".");
                classNameBuilder.append(file.getName().replace(".class", ""));
                classNameSet.add(classNameBuilder.toString());
            }
        }
    }

    /**
     * 加载配置
     *
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {
        InputStream configInputStream = null;
        try {
            configInputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            configContext.load(configInputStream);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            StreamUtil.close(configInputStream);
        }
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public String toLowerFristCase(String str) {
        char[] chars = str.toCharArray();
        if (Character.isUpperCase(chars[0])) {
            chars[0] = Character.toLowerCase(chars[0]);
        }
        return String.valueOf(chars);
    }

    /**
     * 记录requestmapping和method的关系
     */
    private class Handler {
        protected Object controller;
        protected Method method;
        protected Pattern pattern;
        protected Map<String, Integer> paramIndexMapping;

        protected Handler(Pattern pattern, Object controller, Method method) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            paramIndexMapping = new HashMap<>();
            putParamIndexMapping();
        }

        private void putParamIndexMapping() {
            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                for (Annotation a : annotations[i]) {
                    if (a instanceof RequestParam) {
                        String paramName = ((RequestParam) a).value();
                        if (!"".equals(paramName.trim())) {
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }
            //对request和response单独处理
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == HttpServletResponse.class || type == HttpServletRequest.class) {
                    paramIndexMapping.put(type.getName(), i);
                }
            }
        }
    }
}
