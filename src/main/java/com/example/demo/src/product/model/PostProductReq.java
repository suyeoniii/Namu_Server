package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostProductReq {
    private String productName;
    private String imgUrl;
    private int price;
    private int quantity;
    private int categoryIdx;
    private String description;
    private String deadline;
    private String location;
    private String date;
    private String latitude;
    private String longitude;
    //해쉬태그 추가할 것
}
