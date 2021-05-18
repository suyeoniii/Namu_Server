package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "성공"),


    /**
     * 2000 : Request 오류
     */

    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2000, "JWT를 입력해주세요."),
    INVALID_JWT(false, 3000, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2000,"권한이 없는 유저의 접근입니다."),

    //new
    LATITUDE_EMPTY(false, 2001, "위도를 입력해주세요"),
    LONGITUDE_EMPTY(false, 2002, "경도를 입력해주세요"),
    TYPE_ERROR_TYPE(false, 2003, "type은 0~2값을 입력해주세요"),
    DISTANCE_ERROR_TYPE(false, 2004, "거리범위를 확인해주세요"),
    TYPE_EMPTY(false, 2005, "타입을 입력해주세요"),
    QUANTITY_ERROR_TYPE(false, 2006, "수량을 1개 이상 입력해주세요"),
    PRODUCT_NAME_EMPTY(false, 2007, "물품 이름을 입력해주세요"),
    PRODUCT_PRICE_EMPTY(false, 2008, "가격을 입력해주세요"),
    PRODUCT_IMAGE_ERROR_TYPE(false, 2009, "이미지 형식을 확인해주세요"),
    PRODUCT_CATEGORY_EMPTY(false, 2010,  "카테고리를 입력해주세요"),
    PRODUCT_DESCRIPTION_LENGTH(false, 2011, "설명은 최대 500자를 넘길 수 없습니다"),
    PRODUCT_DEADLINE_EMPTY(false, 2012, "마감일을 입력해주세요"),
    PRODUCT_LOCATION_EMPTY(false, 2013, "위치를 입력해주세요"),
    PRODUCT_LATITUDE_EMPTY(false, 2014, "위도를 입력해주세요"),
    PRODUCT_LONGITUDE_EMPTY(false, 2015, "경도를 입력해주세요"),


    USER_USERID_EMPTY(false, 2016, "userIdx를 입력해주세요"),
    USER_USERID_NOT_MATCH(false, 2017, "userIdx가 일치하지 않습니다"),
    PRODUCT_ID_EMPTY(false, 2018, "productIdx를 입력해주세요"),
    PRODUCT_NAME_LENGTH(false, 2019, "물품이름은 최대 50자를 넘길 수 없습니다"),
    PRODUCT_DEADLINE_OVER(false, 2020, "마감일은 1일 이상으로 설정가능합니다"),
    PRODUCT_DEADLINE_ERROR_TYPE(false, 2021, "마감일 형식이 올바르지 않습니다"),
    PRODUCT_DATE_ERROR_TYPE(false, 2022, "date는 요일만 입력가능합니다"),
    SEARCH_QUERY_EMPTY(false, 2023, "검색어를 입력해주세요"),
    PRODUCT_NOT_EXIST(false, 2024, "존재하지 않는 물품입니다"),
    PRODUCT_USER_NOT_MATCH(false, 2025, "존재하지 않는 물품이거나, 등록자가 아닙니다"),


    //
    PRODUCT_QUANTITY_EMPTY(false, 2004, "수량을 입력해주세요"),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),



    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
