package com.flower.ourdiary.controller;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.common.SwaggerResponseError;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.req.ReqUserFcmTokenUpdate;
import com.flower.ourdiary.domain.req.ReqUserSignIn;
import com.flower.ourdiary.domain.req.ReqUserSignUp;
import com.flower.ourdiary.domain.req.ReqUserUpdateMe;
import com.flower.ourdiary.security.SessionUser;
import com.flower.ourdiary.security.UserAuthentication;
import com.flower.ourdiary.service.UserService;
import com.flower.ourdiary.service.UserSignService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

import java.util.List;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserSignService userSignService;
    private final UserService userService;

    @SwaggerResponseError({
            ResponseError.INVALID_EMAIL,
            ResponseError.EXISTS_EMAIL,
            ResponseError.UNEXPECTED_ERROR
    })
    @ApiOperation("가입 (이메일)")
    @PostMapping(value = "/v1/users/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signUpUser(
            @RequestBody ReqUserSignUp reqUserSignUp
    ) {
        userSignService.signUp(reqUserSignUp);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST
    })
    @ApiOperation("로그인 (이메일, 소셜)")
    @PostMapping(value = "/v1/users/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signInUser(
            @RequestBody ReqUserSignIn reqUserSignIn,
            HttpSession session
    ) {

        // TODO Param Validation

        Authentication auth = userSignService.signIn(reqUserSignIn);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        MDC.put(Constant.MDC_KEY_USER_SEQ, ((User)auth.getPrincipal()).getSeq().toString());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({})
    @ApiOperation("로그아웃")
    @PostMapping("/v1/users/signout")
    public ResponseEntity<Void> signOut() {
        throw new IllegalStateException("This Method not working");
    }

    // TODO
//    @PostMapping("/v1/users/signadd")
//    public ResponseEntity<Void> signAddUser(UserSignUp userSignIn) {
//        throw ResponseError.INTERNAL_SERVER_ERROR.exception();
//    }

    @SwaggerResponseError({
            ResponseError.INVALID_QUERY,
            ResponseError.INVALID_PAGE,
            ResponseError.INVALID_PAGE_SIZE
    })
    @ApiOperation("사용자 검색 (리스트)")
    @GetMapping("/v1/users")
    public List<User> getUsers(
            //로그인한거 받아오는 처리
            @ApiIgnore @SessionUser User user,
            @RequestParam("query") String query,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize
    ) {
        //로그인한 사용자 아이디 받아옴,
        return userService.getUserList(user.getSeq(),query,page,pageSize);
    }

    @SwaggerResponseError({
            ResponseError.UNEXPECTED_ERROR
    })
    @ApiOperation("내 정보 조회")
    @GetMapping("/v1/users/me")
    public User getUserMe(
            @ApiIgnore @SessionUser User user
    ) {
        return userService.getUser(user.getSeq());
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.DUPLICATE_USER_NICK
    })
    @ApiOperation("내 정보 수정 (닉네임, 이름)")
    @PutMapping("/v1/users/me")
    public User updateUserMe(
            HttpSession session,
            @ApiIgnore @SessionUser User user,
            @RequestBody ReqUserUpdateMe reqUserUpdateMe
    ) {
        User updatedUser = userService.updateUser(user.getSeq(), reqUserUpdateMe);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(new UserAuthentication(updatedUser));
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        return updatedUser;
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST
    })
    @ApiOperation("내 앱 Fcm 토큰 등록/갱신")
    @PutMapping("/v1/users/fcmtoken")
    public ResponseEntity<Void> updateFcmToken(
            @ApiIgnore @SessionUser User user,
            @RequestBody ReqUserFcmTokenUpdate reqUserFcmTokenUpdate
    ) {
        userService.updateUserFcm(user.getSeq(), reqUserFcmTokenUpdate.getToken());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({})
    @ApiOperation("탈퇴 (임시)")
    @DeleteMapping("/v1/users/me")
    public ResponseEntity<Void> deleteUser(
            HttpSession session,
            @ApiIgnore @SessionUser User user
    ) {
        userSignService.deleteUserAuth(user);
        userService.deleteUser(user);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(new UserAuthentication());
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
