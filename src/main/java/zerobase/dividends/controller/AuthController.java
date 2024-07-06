package zerobase.dividends.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.dividends.model.Auth;
import zerobase.dividends.persist.entity.MemberEntity;
import zerobase.dividends.security.TokenProvider;
import zerobase.dividends.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        MemberEntity result = memberService.register(request);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        MemberEntity member = memberService.authenticate(request);
        String token = tokenProvider.generateToken(member.getUsername(), member.getRoles());

        log.info("user login -> " + request.getUsername());

        return ResponseEntity.ok(token);
    }
}
