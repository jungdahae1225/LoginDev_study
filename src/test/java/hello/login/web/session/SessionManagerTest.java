package hello.login.web.session;

import hello.login.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    SessionManager sessionManager = new SessionManager();
    
    @Test
    void sessionTest() {
        //세션 생성 테스트
        MockHttpServletResponse response = new MockHttpServletResponse(); //MockHttpServletResponse() => 스프링에서 제공하는 테스트용 서블릿
        Member member = new Member();
        sessionManager.createSession(member, response);

        //요청에 응답 쿠키 저장
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        //세션 조회
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);//세션 아이디와 연결되어 있는 member 객체가 맞게 연결되어 있는지

        //세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull(); //삭제 했으니까 이제 null 값이어야함.
    }

}