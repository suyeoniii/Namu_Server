package com.example.demo.src.tag;

import com.example.demo.src.tag.model.GetTagRes;
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
@RequestMapping("/app/tags")
public class TagController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final TagProvider tagProvider;
    @Autowired
    private final TagService tagService;
    @Autowired
    private final JwtService jwtService;


    public TagController(TagProvider tagProvider, TagService tagService, JwtService jwtService) {
        this.tagProvider = tagProvider;
        this.tagService = tagService;
        this.jwtService = jwtService;
    }

    /**
     * 해시태그 조회
     * [GET] /tags
     * @return BaseResponse<List < GetTagRes>>
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetTagRes>> getTags() {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            List<GetTagRes> getTagsRes = tagProvider.getTags(userIdxByJwt);
            return new BaseResponse<>(getTagsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}