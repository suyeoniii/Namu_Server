package com.example.demo.src.tag;


import com.example.demo.src.tag.model.GetTagRes;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TagDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //해시태그 조회
    public List<GetTagRes> selectTags(int userIdx){
        String getTagsQuery = "select UT.idx tagIdx, tagName from UserTag UT inner join Tag T on T.idx=UT.tagIdx" +
                " where UT.userIdx=? and status=0";
        return this.jdbcTemplate.query(getTagsQuery,
                (rs,rowNum) -> new GetTagRes(
                        rs.getInt("tagIdx"),
                        rs.getString("tagName"))
                        , userIdx
        );
    }
}
