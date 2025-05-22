package com.example.OnlineRetailManagement.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class GeneralResponse {
    private String msg;
    private Integer code;
    private HashMap<String, ?> data;
}
