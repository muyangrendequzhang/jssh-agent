package com.myr.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应结果封装，字段与 jssh-back-end 的 com.myr.entity.Result 保持一致，
 * 用于解析后端接口返回的统一响应体 {code, message, data}。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return this.code == 200;
    }
}
