package com.example.demo.src.search;

import com.example.demo.src.product.model.GetProductListRes;
import com.example.demo.src.search.model.GetRecentSearchRes;
import com.example.demo.src.search.model.GetSearchProductRes;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SearchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //최근검색어 조회
    public List<GetRecentSearchRes> getRecentSearch(int userIdx){
        String getSearchQuery = "select idx searchIdx, query from Search S where S.status=0 and userIdx=? order by updatedAt DESC";
        return this.jdbcTemplate.query(getSearchQuery,
                (rs,rowNum) -> new GetRecentSearchRes(
                        rs.getInt("searchIdx"),
                        rs.getString("query")),
                        userIdx
        );
    }

    //물품 검색 결과
    public List<GetSearchProductRes> getSearchProduct(Integer userIdx, int page, int limit, String[] lati, String[] longi, int distance, String[] q){
        int i = 0;
        String selectSearchQuery = "select distinct P.idx, productName, FORMAT(price,0) price, imgUrl,\n" +
                "                    ifnull(view, 0) view , ifnull(wish, 0) wish";
        if (userIdx != null) {
            selectSearchQuery += ",ifnull(isWish, 0) isWish";
        }
        selectSearchQuery += ",CONCAT(CONCAT(ifnull(apply,0),'/'),quantity) apply, delay " +
                " from Product P\n" +
                "  left outer join (select SUM(count) view, productIdx from Viewed group by productIdx) V on V.productIdx=P.idx\n" +
                "  left outer join (select count(*) wish, productIdx";
        if (userIdx != null) {
            selectSearchQuery += " ,count(case when userIdx=" + userIdx + " then 1 else 0 end) isWish";
        }
        selectSearchQuery += " from Wish group by productIdx) W " +
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
            selectSearchQuery += " AND P.userIdx != " + userIdx;
        }
        //카테고리 추가?
        selectSearchQuery += " AND (";
        for(int j = 0; j<q.length; j++){
            if(j != 0)
                selectSearchQuery += " OR";
            selectSearchQuery += " productName  LIKE '%"+ q[j] + "%'";
        }
        selectSearchQuery += ")";

        selectSearchQuery += " ORDER By P.createdAt DESC LIMIT " + page + "," + limit + ";";
        System.out.println(selectSearchQuery);
        if(userIdx!=null){
            return this.jdbcTemplate.query(selectSearchQuery,
                    (rs, rowNum) -> new GetSearchProductRes(
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
            return this.jdbcTemplate.query(selectSearchQuery,
                    (rs, rowNum) -> new GetSearchProductRes(
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
}
