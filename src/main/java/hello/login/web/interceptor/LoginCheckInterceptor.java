package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 화이트리스트 하나하나 체크해주고 하던 일반 서블릿 필터와 달리,
 * 스프링 인터셉터는 인터셉트 등록할 때 등록하면 구현체에서 따로 관리해주지 않아도 된다.
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    //로그인 컨트롤러가 실행되기 <전>에만 인터셉트 검사 하면되기 때문에 preHandle만 구현한다.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 {}", requestURI);
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            log.info("미인증 사용자 요청");
            //로그인으로 redirect 보내버림
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false; //더이상 진행x
        }
        return true;
    }
}
