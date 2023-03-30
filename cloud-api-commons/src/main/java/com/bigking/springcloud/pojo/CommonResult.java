package com.bigking.springcloud.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> implements Serializable {
    Integer code;
    T data;
    String message;

    public CommonResult(Integer code, String message){
        this(code, null, message);
    }

}
