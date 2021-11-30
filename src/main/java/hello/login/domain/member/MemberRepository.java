package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려
 */

//저장소
@Slf4j
@Repository
public class MemberRepository {

    //본래는 인터페이스로 구현하는 것이 좋지만 일단 바로 코딩하도록 한다.
    private static Map<Long, Member> store = new HashMap<>(); //static 사용
    private static long sequence = 0L; //static 사용

    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save: member={}", member);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    //로그인 아이디로 넘어온 파람을 가지고 찾는 것
    public Optional<Member> findByLoginId(String loginId) { //넘어오지 않을 수도 있으니 Optional을 사용

//        List<Member> all = findAll();
//        for(Member m : all) {
//            if(m.getLoginId().equals(loginId)) {
//                return Optional.of(m);
//            }
//        }
//        return Optional.empty(); //java8문법

        return findAll().stream() //list를 stream으로 가져온다.
                .filter(m -> m.getLoginId().equals(loginId)) //filter를 통해 조건에 만족하는 애만 거른다.
                .findFirst(); // 그 중 젤 처음 애 가지고 온다.
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() { //test에서 초기화 하기 위함
        store.clear();
    }
}
