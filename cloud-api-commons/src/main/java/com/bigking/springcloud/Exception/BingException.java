package com.bigking.springcloud.Exception;

public class BingException extends RuntimeException {

    public BingException(){
        super();
    }
    public BingException(String mes,Throwable cause,boolean  str,boolean str1){
        super(mes,cause,str,str1) ;
    }

    public BingException(String mes ,Throwable cause){
        super(mes,cause);
    }
    public BingException(String mes){
        super(mes);
    }

    public BingException(Throwable cause){
        super(cause);
    }

}
