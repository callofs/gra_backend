package com.graProject.graBackend.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限校验注解。
 *
 * <p>
 * 用于标注在 Controller/Service 的类或方法上，在执行前校验当前登录用户是否具备指定角色。
 * </p>
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {

    /**
     * 允许访问的角色列表。
     *
     * <p>
     * 项目约定：1=普通家长，2=认证专家，3=平台管理员。
     * </p>
     *
     * @return 允许访问的角色数组
     */
    int[] roles();
}
