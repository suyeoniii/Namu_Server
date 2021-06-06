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

    //해시태그 알람 등록
    public int insertUserTag(int userIdx, int tagIdx){
        Object[] insertTagParams = new Object[]{userIdx, tagIdx};
        String insertTagQuery = "INSERT INTO UserTag(userIdx, tagIdx) VALUES(?,?);;";
        this.jdbcTemplate.update(insertTagQuery, insertTagParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    //해시태그 존재여부 조회
    public Integer selectTagByName(String tagName){
        String getTagExistQuery = "select exists(select idx from Tag where tagName=?);";
        String getTagsQuery = "select idx from Tag where tagName=?;";

        int isExist = this.jdbcTemplate.queryForObject(getTagExistQuery,
                int.class,
                tagName);
        if(isExist == 1){ //태그 존재시 idx반환
            return this.jdbcTemplate.queryForObject(getTagsQuery,
                    int.class,
                    tagName);
        }
        else{ //태그 존재하지 않으면 0 반환
            return isExist;
        }


    }

    //해시태그 등록
    public int insertTag(String tagName){
        String insertTagQuery = "INSERT INTO Tag(tagName) VALUE(?);";
        this.jdbcTemplate.update(insertTagQuery, tagName);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    //해시태그 중복여부 체크
    public int tagCheck(int userIdx, int tagIdx){
        Object[] tagCheckParams = new Object[]{userIdx, tagIdx};
        String getTagExistQuery = "select exists(select idx from UserTag where userIdx=? and tagIdx=? and status=0);";

        return this.jdbcTemplate.queryForObject(getTagExistQuery,
                int.class,
                tagCheckParams);
    }
}
