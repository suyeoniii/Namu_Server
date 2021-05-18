package com.example.demo.src.product;

import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

//Provider : Read의 비즈니스 로직 처리
@Service
public class ProductProvider {

    private final ProductDao productDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }

    //마감임박 조회
    public List <GetProductListRes> getImminentProducts(Integer userIdx, int page, int limit, String[] lati, String[] longi, int distance) {
        return productDao.selectImminent(userIdx, page, limit, lati, longi, distance);
    }
    //추천상품조회
    public List <GetProductListRes> getRecommendProducts(Integer userIdx, int page, int limit, String[] lati, String[] longi, int distance) {
        return productDao.selectRecommend(userIdx, page, limit, lati, longi, distance);
    }
    //검색 조회


    //물품 상세 조회
    public GetProductRes getProduct(Integer userIdx, int productIdx) {
        return productDao.getProduct(userIdx, productIdx);
    }

    //물품 체크
    public Integer productCheck(int productIdx) {
        return productDao.productCheck(productIdx);
    }

    //찜 체크
    public WishProductCheck wishCheck(Integer userIdx, int productIdx) {
        return productDao.wishCheck(userIdx, productIdx);
    }

    //신청여부 체크
    public Integer applyCheck(Integer userIdx, int productIdx) {
        return productDao.applyCheck(userIdx, productIdx);
    }

    //신청여부 체크
    public List<GetUserAddressRes> getUserAddress(Integer userIdx) {
        return productDao.selectUSerAddress(userIdx);
    }

    //물품, 등록자 체크
    public int productUserCheck(int userIdx, int productIdx) {
        return productDao.productUserCheck(userIdx, productIdx);
    }

}
