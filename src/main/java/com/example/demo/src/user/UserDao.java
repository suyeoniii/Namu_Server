package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserProductRes> getUserApply(int userIdx){
        String getUsersQuery = "SELECT P.idx productIdx, imgUrl, productName, price, A.quantity, P.quantity totalCount, applyCount, deadline FROM Product P\n" +
                "INNER JOIN Apply A ON A.productIdx=P.idx\n" +
                "INNER JOIN (SELECT productIdx, SUM(quantity) applyCount FROM Apply WHERE status=0 GROUP BY productIdx) AP ON AP.productIdx=P.idx\n" +
                "WHERE A.userIdx=?";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("productName"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getInt("totalCount"),
                        rs.getInt("applyCount"),
                        rs.getString("deadline")), userIdx
        );
    }
    //등록물품 조회
    public List<GetUserProductRes> getUserRegister(int userIdx){
        Object[] getUserRegisterParams = new Object[]{userIdx,userIdx};
        String getUsersQuery = "select P.idx productIdx, imgUrl, productName, price, A.quantity, P.quantity totalCount, ifnull(applyCount,0) applyCount, deadline from Product P\n" +
                "left outer join (select productIdx, SUM(quantity) applyCount , case when userIdx=? then quantity else 0  end quantity from Apply group by productIdx) A on A.productIdx=P.idx\n" +
                "where P.userIdx=? order by P.createdAt DESC";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("productName"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getInt("totalCount"),
                        rs.getInt("applyCount"),
                        rs.getString("deadline")), getUserRegisterParams
        );
    }
    //최근본물품 조회
    public List<GetProductRes> getUserViewed(int userIdx){
        String getUsersQuery = "select P.idx productIdx, imgUrl, productName, price, quantity, deadline from Viewed V\n" +
                "inner join Product P on P.idx=V.productIdx where V.userIdx=? order by V.updatedAt DESC";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("productName"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getString("deadline")), userIdx
        );
    }

    //회원정보조회
    public GetUserRes getUser(int userIdx, boolean isMypage){
        String getUsersQuery = "select idx userIdx, nickname, email, phone, profileImg, trust from User where idx=?;";

        if(isMypage){
            return this.jdbcTemplate.queryForObject(getUsersQuery,
                    (rs,rowNum) -> new GetUserRes(
                            rs.getInt("userIdx"),
                            rs.getString("nickname"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("profileImg"),
                            rs.getInt("trust")), userIdx);
        }
        else{
            return this.jdbcTemplate.queryForObject(getUsersQuery,
                    (rs,rowNum) -> new GetUserRes(
                            rs.getInt("userIdx"),
                            rs.getString("nickname"),
                            null,
                            null,
                            rs.getString("profileImg"),
                            rs.getInt("trust")), userIdx);
        }

    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into UserInfo (userName, ID, password, email) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getId(), postUserReq.getPassword(), postUserReq.getEmail()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from UserInfo where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickname = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, password,email,userName,ID from UserInfo where ID = ?";
        String getPwdParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("ID"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("email")
                ),
                getPwdParams
                );

    }



}
