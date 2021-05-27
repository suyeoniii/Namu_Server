package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Records {
    private int productIdx;
    private String productName;
    private String imgUrl;
    private String status;
    private String updatedAt;
}
