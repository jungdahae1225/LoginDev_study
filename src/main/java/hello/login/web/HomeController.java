package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    // @GetMapping("/")
    public String home() {
        return "home";
    }

    //@GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {

        if (memberId == null) {
            return "home";
        }

        //로그인 성공시(쿠키가 있다면)
        Member loginMember = memberRepository.findById(memberId);

        if (loginMember == null) {
            return "home"; //회원이 없다면 그냥 홈으로
        }
        
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        //이제 위의 이전 로직과 달리 세션 정보에 저장된 회원 정보를 조회하여 처리해야 한다.
        Member member = (Member) sessionManager.getSession(request);

        if (member == null) {
            return "home"; //회원이 없다면 그냥 홈으로
        }

        model.addAttribute("member", member);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        //세션이 없으면 home
        HttpSession session = request.getSession(false); //홈화면에서부터 세션을 사용할 필요가 없기 떄문에 불필요한 자원 낭비를 줄이기 위해서 옵션을 false로 준다.

        if (session == null) { //세션이 아예 없으면 아직 회원이 없는 거니까
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {

        /*
        스프링이 지원해주는 어노테이션을 이용해 이 코드들을 위의 파라미터에서 한번에 처리
        //세션이 없으면 home
        HttpSession session = request.getSession(false); //홈화면에서부터 세션을 사용할 필요가 없기 떄문에 불필요한 자원 낭비를 줄이기 위해서 옵션을 false로 준다.

        if (session == null) { //세션이 아예 없으면 아직 회원이 없는 거니까
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        */

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        //실무에서는 Member 객체 자체를 모두 넣지 말고, memberID등으로 최소한의 데이터로 연결하는게 좋다.
        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}