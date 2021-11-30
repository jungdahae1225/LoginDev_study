package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login") //정보가 들어오면 돌리기 시작
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

    //로그아웃 로직
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId"); //쿠키 버리기
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
