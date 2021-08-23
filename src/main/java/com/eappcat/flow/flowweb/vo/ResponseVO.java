package com.eappcat.flow.flowweb.vo;

import lombok.Data;

@Data
public class ResponseVO<T> {
    private int status;
    private T data;

    public static <T> ResponseVO<T> of(T data){
        ResponseVO responseVO= new ResponseVO();
        responseVO.setData(data);
        return responseVO;
    }
}
