package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //@PostMapping("/login") //정보가 들어오면 돌리기 시작
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {

        if (bindingResult.hasErrors()) { //정보가 넘어오지 않으면 다시 로그인 첫화면으로
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword()); //loginService로 확인
        
        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";//정보가 넘어오지 않으면 다시 로그인 첫화면으로
        }

        //로그인 성공 처리 TODO
        //쿠키에 시간 정보를 주지 않으면 세션 쿠키이다.(브라우저 종료시 모두 종료)
        //로그인에 성공하면 쿠키를 생성하고 HttpServletResponse 응답에 담는다. 쿠키 이름은 memberId 이고, 값은
        //회원의 id 를 담아둔다. 웹 브라우저는 종료 전까지 회원의 id 를 서버에 계속 보내줄 것이다
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    //@PostMapping("/login") //정보가 들어오면 돌리기 시작
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {

        if (bindingResult.hasErrors()) { //정보가 넘어오지 않으면 다시 로그인 첫화면으로
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword()); //loginService로 확인

        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";//정보가 넘어오지 않으면 다시 로그인 첫화면으로
        }

        //로그인 성공 처리 TODO
        //세션없이 쿠키만을 사용하던 이전 로직과 달리, 세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관하면 된다.
        /*
        기존 로직
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);*/

        sessionManager.createSession(loginMember,response);

        return "redirect:/";
    }

    //@PostMapping("/login") //정보가 들어오면 돌리기 시작
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) { //이건 request가 필요!!

        if (bindingResult.hasErrors()) { //정보가 넘어오지 않으면 다시 로그인 첫화면으로
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword()); //loginService로 확인

        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";//정보가 넘어오지 않으면 다시 로그인 첫화면으로
        }

        //로그인 성공 처리 TODO
        //세션없이 쿠키만을 사용하던 이전 로직과 달리, 세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관하면 된다.
        /*
        기존 로직
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);*/

        //우리가 만든 세션을 사용하는 경우
        // sessionManager.createSession(loginMember,response);

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성해준다
        HttpSession session = request.getSession();
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

    /**
     * 로그인 이후 redirect 처리
     */
    @PostMapping("/login")
    public String loginV4(
            @Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
            @RequestParam(defaultValue = "/") String redirectURL, //redirect온 url이 없으면 그냥 home으로 보낼 것임
            HttpServletRequest request) {
       
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }
       
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();

        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        //redirectURL 적용
        return "redirect:" + redirectURL; //넘어온 redirectURL을 받아서 그 화면으로 돌아가도록 설정
    }

    //로그아웃 로직
    //@PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId"); //쿠키 버리기
        return "redirect:/";
    }

    //로그아웃 로직
    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) { //여기는 request가 필요하다!!
        //세션을 삭제한다.
        HttpSession session = request.getSession(false); //세션이 이미 없다면 새로 만들게 하지 않기 위해 옵션을 false로 준다.

        if (session != null) {
            session.invalidate(); //세션과 그 안의 데이터 모두 지워준다
        }

        return "redirect:/";
    }

    //로그아웃 로직
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) { //여기는 request가 필요하다!!
        sessionManager.expire(request); //쿠키 버리기
        return "redirect:/";
    }
    /***
     * 쉽게 연습하기 위해 쿠키를 사용한 로그인 세션 유지방법을 선택 했는데 이는 실무에서 보안상의 문제가 심각하게 벌어질 수 있다.
     * => 웹 브라우저에서 임의로 수정이 가능하다.(위변조가 가능하다)
     * => 즉 현재 쿠키의 상태는 클라이언트가 정보를 저장하는 구조이기 때문에 언제든지 훔쳐질 수 있다.
     * 대안책:
     * */
    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0); //쿠키의 유지 시간을 0으로 바꿔 버리는 효과를 낸다.
        response.addCookie(cookie);
    }


}
