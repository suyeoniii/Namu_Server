package com.example.demo.src.product;


import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProductDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetProductRes getProduct(int productIdx){
        return this.jdbcTemplate.queryForObject("select * from Product where idx = ?",
                (rs, rowNum) -> new GetProductRes(
                        rs.getInt("idx"),
                        rs.getString("productName"),
                        rs.getString("description")),
                productIdx);
    }
}
