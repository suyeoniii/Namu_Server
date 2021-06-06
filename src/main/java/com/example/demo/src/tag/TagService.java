package com.example.demo.src.tag;



import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.product.model.PatchProductRes;
import com.example.demo.src.tag.model.PostTagRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class TagService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagDao tagDao;
    private final TagProvider tagProvider;
    private final JwtService jwtService;


    @Autowired
    public TagService(TagDao tagDao, TagProvider tagProvider, JwtService jwtService) {
        this.tagDao = tagDao;
        this.tagProvider = tagProvider;
        this.jwtService = jwtService;

    }

    //태그 등록
    public PostTagRes createTags(int userIdx, String tagName) throws BaseException {
        Integer idx = tagProvider.selectTagByName(tagName);
        if(idx != 0){ //DB에 존재하는 태그면  알림에 추가
            //알림 설정 중복체크
            if(tagProvider.tagCheck(userIdx, idx)==1)
                throw new BaseException(TAG_EXIST);
            return new PostTagRes(tagDao.insertUserTag(userIdx, idx), tagName);
        }
        else{ //DB에 존재하지 않으면 Tag등록 후 알림에 추가
            idx = tagDao.insertTag(tagName);
            return new PostTagRes(tagDao.insertUserTag(userIdx, idx), tagName);
        }

    }

}
