package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.product.model.*;
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
public class ProductService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
    private final ProductProvider productProvider;
    private final JwtService jwtService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public ProductService(ProductDao productDao, ProductProvider productProvider, JwtService jwtService) {
        this.productDao = productDao;
        this.productProvider = productProvider;
        this.jwtService = jwtService;

    }

    //찜 등록,해제
    public WishProductRes wishProduct(Integer userIdx, int productIdx) {
        WishProductCheck wishRes = productProvider.wishCheck(userIdx, productIdx);
        if(wishRes.getCount() == 0){ //create
            return productDao.createWish(userIdx, productIdx);
        }
        else{ //update
            int status = 1;
            if (wishRes.getStatus() == 1) status = 0;
            return productDao.updateWish(userIdx, productIdx, status);
        }
    }

    //물품 신청
    public ApplyProductRes applyProduct(Integer userIdx, int productIdx, int quantity) throws BaseException {
        int productRes = productProvider.productCheck(productIdx);
        int applyRes = productProvider.applyCheck(userIdx, productIdx);

        if(productRes == 0){
            throw new BaseException(PRODUCT_NOT_EXIST);
        }
        if(applyRes == 0){
            return productDao.applyProduct(userIdx, productIdx, quantity);
        }
        return productDao.updateApplyProduct(userIdx, productIdx, quantity);
    }

    //물품 등록
    public PostProductRes createProduct(int userIdx,
                                         String productName,
                                         String imgUrl,
                                         int price,
                                         int quantity,
                                         int categoryIdx,
                                         String description,
                                         String deadline,
                                         String location,
                                         String date,
                                         String latitude,
                                         String longitude) throws BaseException {

        int productIdx = productDao.insertProduct(userIdx,
                productName,
                imgUrl,
                price,
                quantity,
                categoryIdx,
                description,
                deadline,
                location,
                date,
                latitude,
                longitude);

        return new PostProductRes(productIdx);
    }

    //물품 수정
    public PostProductRes updateProduct(int userIdx, int productIdx,
                                        String productName,
                                        String imgUrl,
                                        int price,
                                        int quantity,
                                        int categoryIdx,
                                        String description,
                                        String deadline,
                                        String location,
                                        String date,
                                        String latitude,
                                        String longitude) throws BaseException {

        if(productProvider.productUserCheck(userIdx, productIdx) == 0){
            throw new BaseException(PRODUCT_USER_NOT_MATCH);
        }

        productDao.updateProduct(productIdx,
                productName,
                imgUrl,
                price,
                quantity,
                categoryIdx,
                description,
                deadline,
                location,
                date,
                latitude,
                longitude);

        return new PostProductRes(productIdx);
    }

    //물품 상태 수정
    public PatchProductRes updateProductStatus(int userIdx, int productIdx, int status) throws BaseException {

        if(productProvider.productUserCheck(userIdx, productIdx) == 0){
            throw new BaseException(PRODUCT_USER_NOT_MATCH);
        }

        return productDao.updateProductStatus(productIdx, status);

    }

    //물품 기간 연장
    public PatchProductRes updateProductDeadline(int userIdx, int productIdx) throws BaseException {

        if(productProvider.productUserCheck(userIdx, productIdx) == 0){
            throw new BaseException(PRODUCT_USER_NOT_MATCH);
        }
        if(productProvider.productCheck(productIdx) == 0){
            throw new BaseException(PRODUCT_STATUS_NOT_MATCH);
        }
        if(productProvider.productDeadlineCheck(productIdx) == 0){
            throw new BaseException(PRODUCT_DEADLINE_LEFT);
        }
        return productDao.updateProductDeadline(productIdx);

    }

}
