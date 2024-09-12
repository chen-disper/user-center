package com.yk.model.domain.register;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -6090649565795700590L;
    private  String UserAccount;
    private  String UserPassword;
}
