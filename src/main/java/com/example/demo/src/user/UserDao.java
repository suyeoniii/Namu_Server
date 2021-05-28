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

    public List<GetUserProductRes> getUserApply(int userIdx, int page, int limit){
        Object[] getUserParams = new Object[]{userIdx, page, limit};
        String getUsersQuery = "SELECT P.idx productIdx, imgUrl, productName, price, A.quantity, P.quantity totalCount, applyCount, deadline FROM Product P\n" +
                "INNER JOIN Apply A ON A.productIdx=P.idx\n" +
                "INNER JOIN (SELECT productIdx, SUM(quantity) applyCount FROM Apply WHERE status=0 GROUP BY productIdx) AP ON AP.productIdx=P.idx\n" +
                "WHERE A.userIdx=? limit ?,?";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("productName"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getInt("totalCount"),
                        rs.getInt("applyCount"),
                        rs.getString("deadline")), getUserParams
        );
    }
    //등록물품 조회
    public List<GetUserProductRes> getUserRegister(int userIdx, int page, int limit){
        Object[] getUserRegisterParams = new Object[]{userIdx,userIdx, page, limit};
        String getUsersQuery = "select P.idx productIdx, imgUrl, productName, price, A.quantity, P.quantity totalCount, ifnull(applyCount,0) applyCount, deadline from Product P\n" +
                "left outer join (select productIdx, SUM(quantity) applyCount , case when userIdx=? then quantity else 0  end quantity from Apply group by productIdx) A on A.productIdx=P.idx\n" +
                "where P.userIdx=? order by P.createdAt DESC limit ?,?";
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
    public List<GetProductRes> getUserViewed(int userIdx, int page, int limit){
        Object[] getUserViewedParams = new Object[]{userIdx, page, limit};
        String getUsersQuery = "select P.idx productIdx, imgUrl, productName, price, quantity, deadline from Viewed V\n" +
                "inner join Product P on P.idx=V.productIdx where V.userIdx=? order by V.updatedAt DESC limit ?,?";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("productName"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getString("deadline")), getUserViewedParams
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

    //사용자 주소 조회
    public List<GetAddressRes> getUserAddress(int userIdx){
        String getUsersQuery = "select idx, addressName, latitude, longitude, isChecked from Address where userIdx=?";

            return this.jdbcTemplate.query(getUsersQuery,
                    (rs,rowNum) -> new GetAddressRes(
                            rs.getInt("idx"),
                            rs.getString("addressName"),
                            rs.getString("latitude"),
                            rs.getString("longitude"),
                            rs.getString("isChecked")), userIdx);

    }

    //거래내역 조회
    public GetUserRecordRes getUserRecord(int userIdx){
        String getUserRecordNumQuery = "select count(case when P.status=5 and A.status=0 then 1 end) numOfComplete,\n" +
                "       count(case when A.status=1 then 1 end) numOfCancel\n" +
                "       from Apply A\n" +
                "inner join Product P on P.idx=A.productIdx\n" +
                "where A.userIdx=?";

        String getUserRecordQuery = "select productIdx, productName, imgUrl\n" +
                "     ,case when A.status=0 and P.status=0 then '신청'\n" +
                "         when A.status=0 and P.status=1 then '기간연장'\n" +
                "              when A.status=0 and P.status=2 then '진행확정'\n" +
                "                   when A.status=0 and P.status=3 then '등록자취소'\n" +
                "                        when A.status=0 and P.status=4 then '거래완료'\n" +
                "                             when A.status=1 then '신청자취소'\n" +
                "                                end status\n" +
                "                                 ,case when A.status=1 then DATE_FORMAT(A.updatedAt, '%Y-%m-%d')\n" +
                "                                else DATE_FORMAT(P.updatedAt, '%Y-%m-%d') end updatedAt\n" +
                "from Apply A\n" +
                "inner join Product P on P.idx=A.productIdx\n" +
                "where A.userIdx=? order by updatedAt DESC";

        List<Records> records = this.jdbcTemplate.query(getUserRecordQuery,
                (rs,rowNum) -> new Records(
                        rs.getInt("productIdx"),
                        rs.getString("productName"),
                        rs.getString("imgUrl"),
                        rs.getString("status"),
                        rs.getString("updatedAt")), userIdx);

        return this.jdbcTemplate.queryForObject(getUserRecordNumQuery,
                (rs,rowNum) -> new GetUserRecordRes(
                        rs.getInt("numOfComplete"),
                        rs.getInt("numOfCancel"),
                        records), userIdx);

    }

    //찜한 물품 조회
    public List<GetProductRes> getUserWish(int userIdx, int page, int limit){
        Object[] getUserWishParams = new Object[]{userIdx, page, limit};
        String getUserWishQuery = "select P.idx productIdx, imgUrl, productName, price, quantity, deadline from Wish W\n" +
                "inner join Product P on P.idx=W.productIdx where W.userIdx=? order by W.updatedAt DESC limit ?,?";
        return this.jdbcTemplate.query(getUserWishQuery,
                (rs,rowNum) -> new GetProductRes(
                        rs.getInt("productIdx"),
                        rs.getString("imgUrl"),
                        rs.getString("productName"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getString("deadline")), getUserWishParams
        );
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
