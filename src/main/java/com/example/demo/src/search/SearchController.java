package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.search.model.GetRecentSearchRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/search")
public class SearchController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SearchProvider searchProvider;
    @Autowired
    private final JwtService jwtService;


    public SearchController(SearchProvider searchProvider, JwtService jwtService) {
        this.searchProvider = searchProvider;
        this.jwtService = jwtService;
    }


    /**
     * 최근 검색어 조회 API
     * [GET] /search/recent
     * @return BaseResponse<List <GetRecentSearchRes>>
     */
    @ResponseBody
    @GetMapping("/recent") // (GET) 127.0.0.1:9000/app/products/:productIdx
    public BaseResponse<List<GetRecentSearchRes>> getRecentSearch() {
        // Get Users
        try {
            //jwt에서 idx 추출.
            Integer userIdxByJwt = jwtService.getUserIdxOrNot();
            List<GetRecentSearchRes> getRecentSearchRes = searchProvider.getRecentSearch(userIdxByJwt);
            return new BaseResponse<>(getRecentSearchRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


}

