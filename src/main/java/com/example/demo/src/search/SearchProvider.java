package com.example.demo.src.search;

import com.example.demo.src.product.ProductDao;
import com.example.demo.src.product.model.*;
import com.example.demo.src.search.model.GetRecentSearchRes;
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
public class SearchProvider {

    private final SearchDao searchDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public SearchProvider(SearchDao searchDao, JwtService jwtService) {
        this.searchDao = searchDao;
        this.jwtService = jwtService;
    }

    //최근 검색어 조회
    public List<GetRecentSearchRes> getRecentSearch(int userIdx) {
        return searchDao.getRecentSearch(userIdx);
    }
}

