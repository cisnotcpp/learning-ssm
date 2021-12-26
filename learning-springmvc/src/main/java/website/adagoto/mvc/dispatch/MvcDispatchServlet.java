package website.adagoto.mvc.dispatch;

import website.adagoto.mvc.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc
 * @Description: TODO
 * @date Date : 2021年06月20日 10:44
 */
public class MvcDispatchServlet extends HttpServlet {
    //存放映射关系的
    private Map<String, Object> mapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.dispath(req, resp);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分发
     *
     * @param req
     * @param resp
     */
    protected void dispath(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        uri = uri.replace(contextPath, "").replaceAll("/+", "/");
        if (!mapping.containsKey(uri)) {
            resp.getWriter().write(" 404 NOT FOUND! ");
            return;
        }
        Method method = (Method) mapping.get(uri);
        Map<String, String[]> parameterMap = req.getParameterMap();
        Parameter[] parameters = method.getParameters();
        List<Object> params = new ArrayList<>();
        for (Parameter p : parameters) {
            if (p.getType().toString().contains("javax.servlet.http.HttpServletRequest")) {
                params.add(req);
            } else if (p.getType().toString().contains("javax.servlet.http.HttpServletResponse")) {
                params.add(resp);
            } else {
                String paramName = "";
                if (p.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = p.getAnnotation(RequestParam.class);
                    paramName = requestParam.value();
                }
                if ("".equals(paramName)){
                    paramName = p.getName();
                }
                params.add(parameterMap.get(paramName)[0]);
            }
        }
        method.invoke(mapping.get(method.getDeclaringClass().getName()), params.toArray());
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        InputStream is = null;
        try {
            Properties configContext = new Properties();
            is = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
            configContext.load(is);
            String scanPackageName = configContext.getProperty("scanPackage");
            //获取类
            scanPackage(scanPackageName);
            List<String> classNameList = new ArrayList<>(mapping.keySet());
            for (String className : classNameList) {
                if (!className.contains(".")) {
                    continue;
                }
                //加载类
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    //创建一个实例
                    mapping.put(className, clazz.newInstance());
                    //在扫描url
                    String controllerUrl = "";
                    if (clazz.isAnnotationPresent(RequestMapping.class)) {
                        //获取注解的信息
                        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                        controllerUrl = requestMapping.value();
                    }
                    //对方法进行映射
                    Method[] methods = clazz.getMethods();
                    for (Method m : methods) {
                        if (!m.isAnnotationPresent(RequestMapping.class)) {
                            continue;
                        }
                        RequestMapping requestMapping = m.getAnnotation(RequestMapping.class);
                        StringBuilder methodUrlBuilder = new StringBuilder();
                        methodUrlBuilder.append(controllerUrl);
                        methodUrlBuilder.append("/");
                        methodUrlBuilder.append(requestMapping.value());
                        mapping.put(methodUrlBuilder.toString().replaceAll("/+", "/"), m);
                    }
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();
                    if ("".equals(beanName)) {
                        beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    mapping.put(beanName, instance);
                    for (Class<?> i : clazz.getInterfaces()) {
                        mapping.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
            //进行注入
            for (Object obj : mapping.values()) {
                if (null == obj) {
                    continue;
                }
                Class<?> clazz = obj.getClass();
                if (clazz.isAnnotationPresent(Controller.class)) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field f : fields) {
                        try {
                            if (!f.isAnnotationPresent(Autowired.class)) {
                                continue;
                            }
                            Autowired autowired = f.getAnnotation(Autowired.class);
                            String beanName = autowired.value();
                            if ("".equals(beanName)) {
                                beanName = f.getType().getName();
                            }

                            f.setAccessible(true);
                            f.set(mapping.get(clazz.getName()), mapping.get(beanName));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 扫描包中注解的类型
     *
     * @param packageName
     */
    protected void scanPackage(String packageName) {
        URL resource = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(resource.getFile());
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                scanPackage(packageName + "." + f.getName());
            } else {
                if (!f.getName().endsWith(".class")) {
                    continue;
                }
                String clazzName = packageName + "." + f.getName().replace(".class", "");
                //将扫描到的类放入映射中
                mapping.put(clazzName, null);
            }
        }

    }
}
