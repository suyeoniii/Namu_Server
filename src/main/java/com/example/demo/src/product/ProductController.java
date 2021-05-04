package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.ProductProvider;
import com.example.demo.src.product.ProductService;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.product.model.PostProductRes;
import com.example.demo.src.product.model.WishProductRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

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
     * 물품 상세 조회 API
     * [GET] /products
     * @return BaseResponse<List <GetProductRes>>
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

