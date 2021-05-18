package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductRes {
    private int productIdx;
    private String imgUrl;
    private String productName;
    private int price;
    private int quantity;
    private String deadline;
}
