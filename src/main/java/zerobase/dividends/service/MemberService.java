package zerobase.dividends.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.dividends.model.Auth;
import zerobase.dividends.persist.MemberRepository;
import zerobase.dividends.persist.entity.MemberEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean exists = memberRepository.existsByUsername(member.getUsername());
        if (exists) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));

        MemberEntity result = memberRepository.save(member.toEntity());
        return result;
    }

    // 비밀번호 인증 확인
    public MemberEntity authenticate(Auth.SignIn member) {
        MemberEntity user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
