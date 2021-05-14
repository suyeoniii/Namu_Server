package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserAddressRes {
    private int addressIdx;
    private String latitude;
    private String longitude;
    private String location;
    private int isChecked;
}
