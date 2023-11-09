package com.example.yangbibackend.common.exception;

import com.example.yangbibackend.common.enumeration.ErrorCode;

public class BusinessException extends RuntimeException{

    private final int code;


    public BusinessException(int code,String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode){
        super(ErrorCode.SYSTEM_ERROR.getMessage());
        this.code=errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message){
        super(message);
        this.code=errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}