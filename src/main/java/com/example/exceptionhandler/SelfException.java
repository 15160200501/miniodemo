package com.example.exceptionhandler;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelfException extends RuntimeException{

    @ApiModelProperty(value = "状态码")
    private Integer code;

    @ApiModelProperty(value = "异常信息")
    private String msg;
}
