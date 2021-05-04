package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductRes {
    private int productIdx;
    private String productName;
    private String price;
    private String imgUrl;
    private String description;
    private String view;
    private int wish;
    private int isWish;
    private int isApply;
    private int apply;
    private int quantity;
    private String latitude;
    private String longitude;
    private String deadline;
    private String location;
    private String date;
    private String createdAt;
    private int userIdx;
    private String nickname;
    private String profileImg;
    private int trust;
}
