package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetAddressRes {
    private int idx;
    private String addressName;
    private String latitude;
    private String longitude;
    private String isChecked;
}

