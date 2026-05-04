package com.graProject.graBackend.common.aspect;

import com.graProject.graBackend.common.annotation.HasPermission;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * {@link HasPermission} 注解的切面实现。
 *
 * <p>
 * 在目标方法执行前，从当前请求上下文中获取登录用户信息（由 {@code JwtAuthInterceptor}
 * 写入 {@code request.attribute("loginUser")}），并根据注解声明的角色列表进行权限校验。
 * </p>
 */
@Aspect
@Component
public class HasPermissionAspect {

    /**
     * 请求上下文中保存当前登录用户的 attribute 名称。
     */
    private static final String LOGIN_USER_ATTR = "loginUser";

    /**
     * 执行权限校验。
     *
     * <p>
     * 命中规则：
     * </p>
     * <p>
     * 1) 未登录：抛出 401
     * </p>
     * <p>
     * 2) 已登录但角色不匹配：抛出 403
     * </p>
     *
     * @param joinPoint 切点信息
     */
    @Before("@within(com.graProject.graBackend.common.annotation.HasPermission) || @annotation(com.graProject.graBackend.common.annotation.HasPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        HasPermission hasPermission = resolveAnnotation(joinPoint);
        if (hasPermission == null) {
            return;
        }

        UserDTO loginUser = resolveLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }

        Integer role = loginUser.getRole();
        if (role == null) {
            throw new UserLoginException(HttpCode.FORBIDDEN, HttpCode.FORBIDDEN.getMessage());
        }

        int[] allowRoles = hasPermission.roles();
        if (allowRoles == null || allowRoles.length == 0) {
            throw new UserLoginException(HttpCode.FORBIDDEN, HttpCode.FORBIDDEN.getMessage());
        }

        for (int allowRole : allowRoles) {
            if (role == allowRole) {
                return;
            }
        }

        throw new UserLoginException(HttpCode.FORBIDDEN, HttpCode.FORBIDDEN.getMessage());
    }

    /**
     * 从当前请求上下文解析登录用户。
     *
     * @return 登录用户信息；若不存在（无请求上下文、未写入 attribute 或类型不匹配）返回 null
     */
    private UserDTO resolveLoginUser() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        if (request == null) {
            return null;
        }
        Object loginUser = request.getAttribute(LOGIN_USER_ATTR);
        if (loginUser instanceof UserDTO userDTO) {
            return userDTO;
        }
        return null;
    }

    /**
     * 解析当前方法/类上的 {@link HasPermission} 注解。
     *
     * <p>
     * 优先读取方法上的注解，其次读取类上的注解。
     * </p>
     *
     * @param joinPoint 切点信息
     * @return 注解实例，若未标注则返回 null
     */
    private HasPermission resolveAnnotation(JoinPoint joinPoint) {
        if (!(joinPoint.getSignature() instanceof MethodSignature methodSignature)) {
            return null;
        }

        Method method = methodSignature.getMethod();
        HasPermission onMethod = method.getAnnotation(HasPermission.class);
        if (onMethod != null) {
            return onMethod;
        }

        Class<?> targetClass = joinPoint.getTarget() == null ? null : joinPoint.getTarget().getClass();
        if (targetClass == null) {
            return null;
        }

        return targetClass.getAnnotation(HasPermission.class);
    }
}
