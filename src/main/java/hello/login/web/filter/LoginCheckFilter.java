package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    //filter를 거치지 않을 애들은 필터 풀어줘야함!!
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout","/css/*"};
    
    //init과 destroy 필터는 사용 안할 거라서 뺌
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if (isLoginCheckPath(requestURI)) { //화이트 리스트에 속하지 않는 url이 넘어온다면

                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);

                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) { //세션이 아예 없거나 세션에 없는 사용자라면 회원이 아닌 애가 들어온 것.
                    log.info("미인증 사용자 요청 {}", requestURI);

                    //로그인으로 redirect(로그인페이지로 보내버림)
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI); //로그인으로 보냈는데 사용자가 로그인을 성공했다면 다시 원래 이동하려던 페이지로 넘겨주기 위해 현재 페이지 url도 함께 넘김
                    return; //여기가 중요, 미인증 사용자는 다음으로 진행하지 않고 끝! 다음 컨트롤러와 로직들을 부르지 않고 끝낸다.
                }
            }
            chain.doFilter(request, response);//다음 필터로 넘겨주기
        } catch (Exception e) {
            throw e; //예외 로깅 가능 하지만, 톰캣까지 예외를 보내주어야 함
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI); //requestURI와 화이트 리스트를 비교후 필터를 거르지 않을 애들은 걸려준다.
    }

}
