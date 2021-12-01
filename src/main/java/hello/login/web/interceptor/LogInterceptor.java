package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId"; //이름 상수로 고정해둠
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI(); //넘어온 url 받아옴
        String uuid = UUID.randomUUID().toString();
        request.setAttribute(LOG_ID, uuid); //응답 객체에 정보 셋팅해주기

        //@RequestMapping: HandlerMethod
        //정적 리소스: ResourceHttpRequestHandler
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; //handler에는 호출할 컨트롤러 메서드의모든 정보가 포함되어 넘어온다.
        }
        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler); //로그 찍기 => 스프링 인터셉트는 object 객체로 핸들러가 함께 넘어온다
        return true; //false 진행하지 않는다
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}]", logId, requestURI);

        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}

