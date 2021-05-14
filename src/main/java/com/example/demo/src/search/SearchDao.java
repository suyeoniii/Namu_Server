package com.example.demo.src.search;

import com.example.demo.src.search.model.GetRecentSearchRes;
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

    public List<GetRecentSearchRes> getRecentSearch(int userIdx){
        String getSearchQuery = "select idx searchIdx, query from Search S where S.status=0 and userIdx=? order by updatedAt DESC";
        return this.jdbcTemplate.query(getSearchQuery,
                (rs,rowNum) -> new GetRecentSearchRes(
                        rs.getInt("searchIdx"),
                        rs.getString("query")),
                        userIdx
        );
    }
}
