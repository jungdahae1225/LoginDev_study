package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    public static final String SESSION_COOKIE_NAME = "mySessionId";


    //그냥 헤쉬 맵을 써도 되지만 동시성 문제가 존재 할 때는 이 맴을 쓴다. => ConcurrentHashMap
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>(); //이 Map에서 String 파트는 세션 아이디를, Object는 넘어오는 저장해야 하는 객체 정보를 저장한다.ㄴ
    
    /**
     * 세션 생성로직
     */
    public void createSession(Object value, HttpServletResponse response) {

        //세션 id를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString(); //자바에서 지원해주는 UUID.randomUUID()을 이용하면 유추 불가능한 랜덤 값을 얻을 수 있다.
        sessionStore.put(sessionId, value); //생성된 세션 아이디와 넘어온 value를 넣어준다.

        //이제 세션로직 만든 후 쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        
        //응답에 쿠키 넣어주기
        response.addCookie(mySessionCookie);
    }

    /**
     * 세션 조회로직
     */
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie == null) { //쿠키를 찾지 못했으면 null
            return null;
        }
        //찾았으면 쿠키와 맞는 member 객체 return 
        return sessionStore.get(sessionCookie.getValue());
    }
    /**
     * 세션 만료로직
     */
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        
        if (sessionCookie != null) { //찾은 쿠키 값이 있으면 해당 쿠키값을 지우기
            sessionStore.remove(sessionCookie.getValue());
        }
    }
    
    //세션에서 쿠키 찾아오는 로직
    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies()) //응답에서 쿠기가 배열로 넘어오는데,이를 stream으로 바꾸고 -> filter를 통해 쿠키의 정보와 일치하는
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null); //없으면 null
    }
}

