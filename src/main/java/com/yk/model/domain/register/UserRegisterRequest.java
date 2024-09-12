package com.yk.model.domain.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "用户注册实体类")
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -6090649565795700590L;
    @ApiModelProperty(name = "userAccount", value = "用户账号名", example = "disper")
    private String UserAccount;
    @ApiModelProperty(name = "userPassword", value = "用户密码", example = "123456")
    private String UserPassword;
    @ApiModelProperty(name = "checkPassword", value = "确认密码", example = "123456")
    private String checkPassword;
    @ApiModelProperty(name = "planetCoe", value = "用户编号", example = "1")
    private String planetCode;
}
