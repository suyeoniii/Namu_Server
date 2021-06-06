package com.example.demo.src.tag;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.tag.model.GetTagRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class TagProvider {

    private final TagDao tagDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TagProvider(TagDao tagDao, JwtService jwtService) {
        this.tagDao = tagDao;
        this.jwtService = jwtService;
    }

    //신청물품 조회
    public List<GetTagRes> getTags(int userIdx) throws BaseException{
        try{
            List<GetTagRes> getTagRes = tagDao.selectTags(userIdx);
            return getTagRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
