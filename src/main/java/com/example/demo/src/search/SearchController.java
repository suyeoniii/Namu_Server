package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.ProductProvider;
import com.example.demo.src.product.model.GetProductListRes;
import com.example.demo.src.product.model.GetUserAddressRes;
import com.example.demo.src.search.model.GetRecentSearchRes;
import com.example.demo.src.search.model.GetSearchProductRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/search")
public class SearchController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SearchProvider searchProvider;
    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final JwtService jwtService;


    public SearchController(SearchProvider searchProvider, JwtService jwtService, ProductProvider productProvider) {
        this.searchProvider = searchProvider;
        this.jwtService = jwtService;
        this.productProvider = productProvider;
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

    /**
     * 검색 조회
     * [GET] /search?q=
     * @return BaseResponse<List <GetRecentSearchRes>>
     */
    @ResponseBody
    @GetMapping("/{type}")
    public BaseResponse<List<GetSearchProductRes>> getSearchProduct(@PathVariable("type") int type, @RequestParam(required = false) String q,
                                                                    @RequestParam(required = false, defaultValue = "1") String page,
                                                                    @RequestParam(required = false, defaultValue = "20") String limit,
                                                                    @RequestParam(required = false) String[] lati,
                                                                    @RequestParam(required = false) String[] longi,
                                                                    @RequestParam(required = false, defaultValue = "0") String distance) {
        // Get Users
        try {
            //jwt에서 idx 추출.
            Integer userIdx = jwtService.getUserIdxOrNot();
            //로그인 시 사용자 위도경도 가져오기
            if(userIdx != null){
                List<GetUserAddressRes> addressRes = productProvider.getUserAddress(userIdx);
            }
            //페이징 기본값 설정
            int start = Integer.parseInt(limit)*(Integer.parseInt(page)-1);

            //distance 값 변환
            int dis;
            if(distance.equals("0")){
                dis = 100;
            }
            else if(distance.equals("1")){
                dis = 300;
            }
            else if(distance.equals("2")){
                dis = 500;
            }
            else if(distance.equals("3")){
                dis = 1;
            }
            else if(distance.equals("4")){
                dis = 3;
            }
            else{
                return new BaseResponse<>(DISTANCE_ERROR_TYPE);
            }

            if(q.length()==0)
                return new BaseResponse<>(SEARCH_QUERY_EMPTY);

            //query처리
            String[] query = q.split(" ");

            if(type==0){ //일반 물품 조회
                List<GetSearchProductRes> getProductRes = searchProvider.getSearchProduct(
                        userIdx, start, Integer.parseInt(limit), lati, longi, dis, query);
                return new BaseResponse<>(getProductRes);
            }
            else if(type==1){ //추천상품 조회

            }
            return new BaseResponse<>(TYPE_EMPTY);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


}

