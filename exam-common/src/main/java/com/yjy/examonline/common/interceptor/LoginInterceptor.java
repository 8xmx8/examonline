package com.yjy.examonline.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    private Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("request:" + request.getRequestURI());
        //一旦拦截器这个方法执行，表示此次的请求需要做认证。
        Object teacher = request.getSession().getAttribute("loginTeacher");
        Object student = request.getSession().getAttribute("loginStudent");
        if (teacher == null && student == null) {
            //还没有登录，请求不能继续,需要返回登录页面
            request.getRequestDispatcher("/common/timeout.html").forward(request, response);
            
            log.debug("intercepted");
            return false;
        }
        //有登录信息，请求可以继续
        log.debug("past");
        return true;
    }
}
