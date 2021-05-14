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

    //마감임박 물품 조회
    public List<GetProductListRes> selectImminent(Integer userIdx, int page, int limit, String[] lati, String[] longi, int distance) {

        int i = 0;
            String selectImminentQuery = "select distinct P.idx, productName, FORMAT(price,0) price, imgUrl,\n" +
                    "                    ifnull(view, 0) view , ifnull(wish, 0) wish";
            if (userIdx != null) {
                selectImminentQuery += ",ifnull(isWish, 0) isWish";
            }
            selectImminentQuery += ",CONCAT(CONCAT(ifnull(apply,0),'/'),quantity) apply, delay " +
                    " from Product P " +
                    " left outer join (select SUM(count) view, productIdx from Viewed group by productIdx) V on V.productIdx=idx " +
                    " left outer join (select count(*) wish, productIdx";
            if (userIdx != null) {
                selectImminentQuery += " ,count(case when userIdx=" + userIdx + " then 1 else 0 end) isWish";
            }
            selectImminentQuery += " from Wish group by productIdx) W " +
                    " on W.productIdx =idx " +
                    " inner join (SELECT idx, Round((6371*acos(cos(radians(" +
                    lati[i] +
                    " ))*cos(radians(latitude))*cos(radians(longitude) -radians(" + longi[i] + "))+sin(radians(" + lati[i] + "))*sin(radians(latitude)))),2) " +
                    " AS distance " +
                    " FROM Product " +
                    " Having distance <= " + distance + ") dis on dis.idx=P.idx " +
                    " left outer join (select productIdx, count(*) apply from Apply group by productIdx) AP on AP.productIdx = P.idx " +
                    " WHERE TIMESTAMPDIFF(DAY, deadline, current_timestamp()) >= -3 " +
                    " AND deadline > current_timestamp()";
            if (userIdx != null) {
                selectImminentQuery += " AND P.userIdx != " + userIdx + "";
            }
            selectImminentQuery += " ORDER BY delay LIMIT " + page + "," + limit + ";";

            if(userIdx!=null){
                return this.jdbcTemplate.query(selectImminentQuery,
                        (rs, rowNum) -> new GetProductListRes(
                                rs.getInt("idx"),
                                rs.getString("productName"),
                                rs.getString("price"),
                                rs.getString("imgUrl"),
                                rs.getString("view"),
                                rs.getInt("wish"),
                                rs.getInt("isWish"),
                                rs.getString("apply"),
                                rs.getInt("delay")));
            }
            else{
                return this.jdbcTemplate.query(selectImminentQuery,
                        (rs, rowNum) -> new GetProductListRes(
                                rs.getInt("idx"),
                                rs.getString("productName"),
                                rs.getString("price"),
                                rs.getString("imgUrl"),
                                rs.getString("view"),
                                rs.getInt("wish"),
                                0,
                                rs.getString("apply"),
                                rs.getInt("delay")));
            }

        }

    //추천상품
    public List<GetProductListRes> selectRecommend(Integer userIdx, int page, int limit, String[] lati, String[] longi, int distance) {

        int i = 0;
        String selectRecommendQuery = "select distinct P.idx, productName, FORMAT(price,0) price, imgUrl,\n" +
                "                    ifnull(view, 0) view , ifnull(wish, 0) wish";
        if (userIdx != null) {
            selectRecommendQuery += ",ifnull(isWish, 0) isWish";
        }
        selectRecommendQuery += ",CONCAT(CONCAT(ifnull(apply,0),'/'),quantity) apply, delay " +
                " from Product P\n" +
                "      inner join RecommendProduct RP on RP.productIdx = P.idx\n" +
                "  left outer join (select SUM(count) view, productIdx from Viewed group by productIdx) V on V.productIdx=P.idx\n" +
                "  left outer join (select count(*) wish, productIdx";
        if (userIdx != null) {
            selectRecommendQuery += " ,count(case when userIdx=" + userIdx + " then 1 else 0 end) isWish";
        }
        selectRecommendQuery += " from Wish group by productIdx) W " +
                " on W.productIdx=P.idx " +
                " inner join (SELECT idx, Round((6371*acos(cos(radians(" +
                lati[i] +
                " ))*cos(radians(latitude))*cos(radians(longitude) -radians(" + longi[i] + "))+sin(radians(" + lati[i] + "))*sin(radians(latitude)))),2) " +
                " AS distance " +
                " FROM Product " +
                " Having distance <= " + distance + ") dis on dis.idx=P.idx " +
                " left outer join (select productIdx, count(*) apply from Apply group by productIdx) AP on AP.productIdx = P.idx " +
                " WHERE deadline > current_timestamp()";
        if (userIdx != null) {
            selectRecommendQuery += " AND P.userIdx != " + userIdx + " AND RP.userIdx= " + userIdx;
        }
        selectRecommendQuery += " ORDER By rand() LIMIT " + page + "," + limit + ";";

        if(userIdx!=null){
            return this.jdbcTemplate.query(selectRecommendQuery,
                    (rs, rowNum) -> new GetProductListRes(
                            rs.getInt("idx"),
                            rs.getString("productName"),
                            rs.getString("price"),
                            rs.getString("imgUrl"),
                            rs.getString("view"),
                            rs.getInt("wish"),
                            rs.getInt("isWish"),
                            rs.getString("apply"),
                            rs.getInt("delay")));
        }
        else{
            return this.jdbcTemplate.query(selectRecommendQuery,
                    (rs, rowNum) -> new GetProductListRes(
                            rs.getInt("idx"),
                            rs.getString("productName"),
                            rs.getString("price"),
                            rs.getString("imgUrl"),
                            rs.getString("view"),
                            rs.getInt("wish"),
                            0,
                            rs.getString("apply"),
                            rs.getInt("delay")));
        }

    }

    //상세조회
    public GetProductRes getProduct(Integer userIdx, int productIdx){
        Object[] getProductParams = new Object[]{userIdx, userIdx, productIdx, productIdx};

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
            return this.jdbcTemplate.queryForObject("select distinct P.idx productIdx, productName, FORMAT(price,0) price, imgUrl, description" +
                            "  ,ifnull(view, 0) view , ifnull(wish, 0) wish\n" +
                            " ,ifnull(isWish, 0) isWish\n" +
                            " ,ifnull(apply,0) apply, isApply, quantity\n" +
                            "  ,latitude, longitude, DATE_FORMAT(deadline, '%Y-%m-%d') deadline, location, date, DATE_FORMAT(P.createdAt, '%Y-%m-%d') createdAt\n" +
                            "  ,U.idx userIdx, nickname, U.profileImg, U.trust\n" +
                            "from Product P\n" +
                            "left outer join (select SUM(count) view, productIdx from Viewed group by productIdx) V on V.productIdx=P.idx\n" +
                            "left outer join (select count(*) wish, productIdx\n" +
                            ",count(case when userIdx=? then 1 else 0 end) isWish\n" +
                            " from Wish group by productIdx) W on W.productIdx =P.idx\n" +
                            "left outer join (select productIdx, count(*) apply, count(case when userIdx=? and productIdx=? then 1 end) isApply from Apply group by productIdx) AP on AP.productIdx = P.idx\n" +
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
                            rs.getInt("trust"))
                    ,getProductParams);
        }
    }

    //물품 체크
    public int productCheck(int productIdx){
        String productQuery = "select exists(select idx from Product where idx=? and status=0);";
        return this.jdbcTemplate.queryForObject(productQuery,
                int.class,
                productIdx);
    }
    //신청 체크
    public int applyCheck(Integer userIdx, int productIdx){
        Object[] applyProductParams = new Object[]{userIdx, productIdx};
        String checkApplyQuery = "select exists(select idx from Apply where userIdx=? and productIdx=?);";
        return this.jdbcTemplate.queryForObject(checkApplyQuery,
                int.class,
                applyProductParams);
    }
    //찜 체크
    public WishProductCheck wishCheck(Integer userIdx, int productIdx){
        Object[] wishProductParams = new Object[]{userIdx, productIdx};
        String checkWishQuery = "select count(*) count, status from Wish where userIdx=? and productIdx=?;";
        return this.jdbcTemplate.queryForObject(checkWishQuery,
                (rs, rowNum) -> new WishProductCheck(
                        rs.getInt("count"),
                        rs.getInt("status")),
                wishProductParams);
    }
    //신청 업데이트
    public ApplyProductRes updateApplyProduct(Integer userIdx, int productIdx, int quantity){
        Object[] applyProductParams = new Object[]{quantity, userIdx, productIdx};
        String applyProductQuery = "UPDATE Apply SET quantity = ? WHERE userIdx=? AND productIdx=?";
        this.jdbcTemplate.update(applyProductQuery, applyProductParams);
        return new ApplyProductRes(productIdx);
    }
    //wish 등록
    public WishProductRes createWish(Integer userIdx, int productIdx){
        Object[] wishProductParams = new Object[]{userIdx, productIdx};
        String createWishQuery = "INSERT INTO Wish(userIdx, productIdx) VALUES(?, ?)";
        this.jdbcTemplate.update(createWishQuery, wishProductParams);
        return new WishProductRes(1);
    }
    //wish 업데이트
    public WishProductRes updateWish(Integer userIdx, int productIdx, Integer status){
        Object[] wishProductParams = new Object[]{status, userIdx, productIdx};
        String createWishQuery = "UPDATE Wish SET status=? WHERE userIdx=? and productIdx=?;";
        this.jdbcTemplate.update(createWishQuery, wishProductParams);
        int result = 0;
        if(status==0) result = 1;
        return new WishProductRes(result);
    }
    public ApplyProductRes applyProduct(Integer userIdx, int productIdx, int quantity){
        Object[] applyProductParams = new Object[]{userIdx, productIdx, quantity};
        String applyProductQuery = "INSERT INTO Apply(userIdx,productIdx,quantity) VALUES(?, ?, ?)";
        this.jdbcTemplate.update(applyProductQuery, applyProductParams);
        return new ApplyProductRes(productIdx);
    }
    //사용자 주소 조회
    public List<GetUserAddressRes> selectUSerAddress(Integer userIdx){
        String selectUSerAddressQuery = "select count(*) count, status from Wish where userIdx=? and productIdx=?;";
        return this.jdbcTemplate.query(selectUSerAddressQuery,
                (rs, rowNum) -> new GetUserAddressRes(
                        rs.getInt("addressIdx"),
                        rs.getString("latitude"),
                        rs.getString("longitude"),
                        rs.getString("location"),
                        rs.getInt("count")),
                userIdx);
    }
}
