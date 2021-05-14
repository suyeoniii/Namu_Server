package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductListRes {
    private int idx;
    private String productName;
    private String price;
    private String imgUrl;
    private String view;
    private int wish;
    private int isWish;
    private String apply;
    private int delay;
}
