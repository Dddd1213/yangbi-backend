package com.example.yangbibackend.common.result;

import com.example.yangbibackend.common.enumeration.ErrorCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public Result(int code, String message,T data){
        this.code=code;
        this.message=message;
        this.data=data;
    }

    public Result(ErrorCode errorCode) {
        this(errorCode.getCode(),  errorCode.getMessage(),null);
    }

    public Result(int code, String message) {
        this(code, message,null );
    }



}
