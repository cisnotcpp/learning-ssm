package website.adagoto.framework.aop;

import lombok.Data;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: 用于存储AOP的配置
 * @date Date : 2021年07月04日 10:49
 */
@Data
public class AopConfig {
    /**
     * 切面表达式
     */
    private String pointCut;
    /**
     * 前置通知的方法名
     */
    private String aspectBefore;
    /**
     * 后置通知的方法名
     */
    private String aspectAfter;
    /**
     * 织入的切面类
     */
    private String aspectClass;
    /**
     * 异常通知的方法名
     */
    private String aspectAfterThrow;
    /**
     * 通知的异常类型
     */
    private String aspectAfterThrowingName;
}
