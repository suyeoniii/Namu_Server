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
        Integer status = productProvider.wishCheck(userIdx, productIdx);

        if(status == null){ //create
            return productDao.createWish(userIdx, productIdx);
        }
        else{ //update
            if (status == 1) status = 0;
            else status = 1;
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
}
