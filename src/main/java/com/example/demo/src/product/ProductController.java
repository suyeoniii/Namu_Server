package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/products")
public class ProductController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;


    public ProductController(ProductProvider productProvider, ProductService productService, JwtService jwtService) {
        this.productProvider = productProvider;
        this.productService = productService;
        this.jwtService = jwtService;
    }

    /**
     * 물품 조회 API (마감임박, 추천상품)
     * [GET] /products
     * @return BaseResponse<List <GetProductListRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/products/:productIdx
    public BaseResponse<List<GetProductListRes>> getProductList(@RequestParam(required = true) int type,
                                                                @RequestParam(required = false, defaultValue = "1") String page,
                                                                @RequestParam(required = false, defaultValue = "20") String limit,
                                                                @RequestParam(required = false) String[] lati,
                                                                @RequestParam(required = false) String[] longi,
                                                                @RequestParam(required = false, defaultValue = "0") String distance) {
        // 마감임박
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

            if(type==0){ //마감임박 조회
                List<GetProductListRes> getProductRes = productProvider.getImminentProducts(
                        userIdx, start, Integer.parseInt(limit), lati, longi, dis);
                return new BaseResponse<>(getProductRes);
            }
            else if(type==1){ //추천상품 조회
                //List<GetProductListRes> getProductRes = productProvider.getRecommendProducts(
                        //userIdxByJwt, page, limit, lati, longi, distance);
                //return new BaseResponse<>(getProductRes);
            }
            return new BaseResponse<>(TYPE_EMPTY);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 물품 상세 조회 API
     * [GET] /products
     * @return BaseResponse<GetProductRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{productIdx}") // (GET) 127.0.0.1:9000/app/products/:productIdx
    public BaseResponse<GetProductRes> getProduct(@PathVariable("productIdx") int productIdx) {
        // Get Users
        try {
            //jwt에서 idx 추출.
            Integer userIdxByJwt = jwtService.getUserIdxOrNot();
            GetProductRes getProductRes = productProvider.getProduct(userIdxByJwt, productIdx);
            return new BaseResponse<>(getProductRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 신청 등록 API
     * [POST] /products/:productIdx
     *
     * @return BaseResponse<List <ApplyProductRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/{productIdx}") // (POST) 127.0.0.1:9000/app/products/:productIdx
    public BaseResponse<ApplyProductRes> applyProduct(@PathVariable("productIdx") int productIdx, @RequestBody ApplyProductReq applyProductReq) {
        try {
            //quantity 빈 값 체크
            if(applyProductReq.getQuantity()==0)
                return new BaseResponse<>(PRODUCT_QUANTITY_EMPTY);

            Integer userIdxByJwt = jwtService.getUserIdx();

            ApplyProductRes applyProductRes = productService.applyProduct(userIdxByJwt, productIdx, applyProductReq.getQuantity());
            return new BaseResponse<>(applyProductRes) ;
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 찜 등록/해제 API
     * [POST] /products/:productIdx/wish
     *
     * @return BaseResponse<List <WishProductRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/{productIdx}/wish") // (GET) 127.0.0.1:9000/app/products/:productIdx
    public BaseResponse<WishProductRes> wishProduct(@PathVariable("productIdx") int productIdx) {
        // 찜 등록/해제
        try {
            //jwt에서 idx 추출.
            Integer userIdxByJwt = jwtService.getUserIdx();
            WishProductRes wishProductRes = productService.wishProduct(userIdxByJwt, productIdx);
            return new BaseResponse<>(wishProductRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}

