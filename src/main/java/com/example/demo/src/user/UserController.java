package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 신청물품 조회
     * [GET] /users/:userIdx/apply
     * 사용자 신청물품 조회
     * @return BaseResponse<List<GetUserApplyRes>>
     */
    //Path variable
    @ResponseBody
    @GetMapping("/{userIdx}/apply") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserProductRes>> getUserApply(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx == 0){
                return new BaseResponse<>(USER_USERID_EMPTY);
            }
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(USER_USERID_NOT_MATCH);
            }
            // Get User Apply
            List<GetUserProductRes> getUsersRes = userProvider.getUserApply(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * @return BaseResponse <GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        try{
            Integer userIdxByJwt = jwtService.getUserIdxOrNot();
            boolean isMypage = false;
            if(userIdxByJwt!=null && userIdxByJwt == userIdx){
                isMypage = true;
            }
            GetUserRes getUsersRes = userProvider.getUser(userIdx, isMypage);
            return new BaseResponse<>(getUsersRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 등록물품 조회
     * [GET] /users/:userIdx/register
     * @return BaseResponse<List<GetUserProductRes>>
     */
    //Path variable
    @ResponseBody
    @GetMapping("/{userIdx}/register") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserProductRes>> getUserRegister(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx == 0){
                return new BaseResponse<>(USER_USERID_EMPTY);
            }
            // Get User Apply
            List<GetUserProductRes> getUsersRes = userProvider.getUserRegister(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 최근본 물품 조회
     * [GET] /users/:userIdx/viewed
     * @return BaseResponse<List<GetProductRes>>
     */
    //Path variable
    @ResponseBody
    @GetMapping("/{userIdx}/viewed") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetProductRes>> getUserViewed(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx == 0){
                return new BaseResponse<>(USER_USERID_EMPTY);
            }
            List<GetProductRes> getUsersRes = userProvider.getUserViewed(userIdx);
            String jwt = jwtService.createJwt(userIdx);
            System.out.println(jwt);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 주소 조회
     * [GET] /users/:userIdx/address
     * @return BaseResponse<List<GetAddressRes>>
     */
    //Path variable
    @ResponseBody
    @GetMapping("/{userIdx}/address")
    public BaseResponse<List<GetAddressRes>> getUserGetAddressRes(@PathVariable("userIdx") int userIdx) {
        try{
            if(userIdx == 0){
                return new BaseResponse<>(USER_USERID_EMPTY);
            }
            Integer userIdxByJwt = jwtService.getUserIdx();
            if(userIdxByJwt!=userIdx)
                return new BaseResponse<>(USER_USERID_NOT_MATCH);

            List<GetAddressRes> getUsersRes = userProvider.getUserAddress(userIdx);
            String jwt = jwtService.createJwt(userIdx);
            System.out.println(jwt);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getNickname());
            userService.modifyUserName(patchUserReq);

            String result = "";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
