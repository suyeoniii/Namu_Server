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

    public GetProductRes getProduct(Integer userIdx, int productIdx){
        Object[] getProductParams = new Object[]{userIdx, productIdx};

        if(userIdx == null){ //로그인 아닌경우
            return this.jdbcTemplate.queryForObject("select distinct P.idx productIdx, productName, FORMAT(price,0) price, imgUrl,description\n" +
                            "  ,ifnull(view, 0) view , ifnull(wish, 0) wish\n" +
                            "  ,ifnull(apply,0) apply, quantity\n" +
                            "  ,latitude, longitude, DATE_FORMAT(deadline, '%Y-%m-%d') deadline, location, date, DATE_FORMAT(P.createdAt, '%Y-%m-%d') createdAt\n" +
                            "  ,U.idx userIdx, nickname, U.profileImg, U.trust\n" +
                            "from Product P\n" +
                            "left outer join (select SUM(count) view, productIdx from Viewed group by productIdx) V on V.productIdx=P.idx\n" +
                            "left outer join (select count(*) wish, productIdx\n" +
                            "from Wish group by productIdx) W on W.productIdx =P.idx\n" +
                            "left outer join (select productIdx, count(*) apply from Apply group by productIdx) AP on AP.productIdx = P.idx\n" +
                            "inner join User U on P.userIdx=U.idx\n" +
                            "WHERE P.idx=?",
                    (rs, rowNum) -> new GetProductRes(
                            rs.getInt("productIdx"),
                            rs.getString("productName"),
                            rs.getString("price"),
                            rs.getString("imgUrl"),
                            rs.getString("description"),
                            rs.getString("view"),
                            rs.getInt("wish"),
                            0,
                            0,
                            rs.getInt("apply"),
                            rs.getInt("quantity"),
                            rs.getString("latitude"),
                            rs.getString("longitude"),
                            rs.getString("deadline"),
                            rs.getString("location"),
                            rs.getString("date"),
                            rs.getString("createdAt"),
                            rs.getInt("userIdx"),
                            rs.getString("nickname"),
                            rs.getString("profileImg"),
                            rs.getInt("trust")),
                    productIdx);
        }
        else{  //로그인 경우
            return this.jdbcTemplate.queryForObject("select distinct P.idx productIdx, productName, FORMAT(price,0) price, imgUrl,\n" +
                            "  ifnull(view, 0) view , ifnull(wish, 0) wish`;\n" +
                            " `,ifnull(isWish, 0) isWish`;\n" +
                            "`,ifnull(apply,0) apply, quantity\n" +
                            "  ,latitude, longitude, DATE_FORMAT(deadline, '%Y-%m-%d') deadline, location, date, DATE_FORMAT(P.createdAt, '%Y-%m-%d') createdAt\n" +
                            "  ,U.idx userIdx, nickname, U.profileImg, U.trust\n" +
                            "from Product P\n" +
                            "left outer join (select SUM(count) view, productIdx from Viewed group by productIdx) V on V.productIdx=P.idx\n" +
                            "left outer join (select count(*) wish, productIdx`;\n" +
                            " `, count(case when userIdx=$? then 1 else 0 end) isWish`;\n" +
                            " ` from Wish group by productIdx) W on W.productIdx =P.idx\n" +
                            "left outer join (select productIdx, count(*) apply from Apply group by productIdx) AP on AP.productIdx = P.idx\n" +
                            "inner join User U on P.userIdx=U.idx\n" +
                            "WHERE P.idx=?",
                    (rs, rowNum) -> new GetProductRes(
                            rs.getInt("productIdx"),
                            rs.getString("productName"),
                            rs.getString("price"),
                            rs.getString("imgUrl"),
                            rs.getString("description"),
                            rs.getString("view"),
                            rs.getInt("wish"),
                            rs.getInt("isWish"),
                            rs.getInt("isApply"),
                            rs.getInt("apply"),
                            rs.getInt("quantity"),
                            rs.getString("latitude"),
                            rs.getString("longitude"),
                            rs.getString("deadline"),
                            rs.getString("location"),
                            rs.getString("date"),
                            rs.getString("createdAt"),
                            rs.getInt("userIdx"),
                            rs.getString("nickname"),
                            rs.getString("profileImg"),
                            rs.getInt("trust")),
                    getProductParams);
        }
    }

    public WishProductRes createWish(Integer userIdx, int productIdx){
        Object[] wishProductParams = new Object[]{userIdx, productIdx};
        String createWishQuery = "INSERT INTO Wish(userIdx, productIdx) VALUES(?, ?)";
        this.jdbcTemplate.update(createWishQuery, wishProductParams);
        return new WishProductRes(1);
    }

    public Integer wishCheck(Integer userIdx, int productIdx){
        Object[] wishProductParams = new Object[]{userIdx, productIdx};
        String checkWishQuery = "select idx, status from Wish where userIdx=? and productIdx=?;";
        return this.jdbcTemplate.queryForObject("select idx, status, count(case when status=0 then 1 end) count from Wish where userIdx=? and productIdx=?;",
                (rs, rowNum) ->
                        rs.getInt("status"),
                wishProductParams);
    }
    public WishProductRes updateWish(Integer userIdx, int productIdx, Integer status){
        Object[] wishProductParams = new Object[]{status, userIdx, productIdx};
        String createWishQuery = "UPDATE Wish SET status=? WHERE userIdx=? and productIdx=?;";
        this.jdbcTemplate.update(createWishQuery, wishProductParams);
        return new WishProductRes(status);
    }
}
