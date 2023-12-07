package com.bigking.springcloud.Exception;

public class CommonException extends Exception{

    public CommonException(){
        super();
    }
    public CommonException(String mes,Throwable cause, boolean  str,boolean str1){
        super(mes,cause,str,str1) ;
    }

    public CommonException(String mes ,Throwable cause){
        super(mes,cause);
    }
    public CommonException(String mes){
        super(mes);
    }

    public CommonException(Throwable cause){
        super(cause);
    }
}
