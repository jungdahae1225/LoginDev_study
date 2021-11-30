package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service //빈 등록
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    /**
     * @return null이면 로그인 실패
     */
    public Member login(String loginId, String password) {
        //로그인 아이디로 회원 등록이 되어있는지 여부 확인
        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);
        Member member = findMemberOptional.get();

        if(member.getPassword().equals(password)) { //넘어온 비밀번호 정보가 저장된 비밀번호 정보와 같으면
            return member;
        }else {
            return null;
        }

        /*return memberRepository.findByLoginId(loginId) //로그인 아이디로 회원 등록이 되어있는지 여부 확인
                .filter(m -> m.getPassword().equals(password))
                .orElse(null); //filer로 걸러지지 않았으면 null반환
        */
    }
}
