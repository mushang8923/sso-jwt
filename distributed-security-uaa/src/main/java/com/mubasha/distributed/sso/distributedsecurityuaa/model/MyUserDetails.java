package com.mubasha.distributed.sso.distributedsecurityuaa.model;

import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class MyUserDetails extends User {

    private String addtionObject;

    private String type;

    private String loginVersion;


    public MyUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public String getAddtionObject() {
        return addtionObject;
    }

    public void setAddtionObject(String addtionObject) {
        this.addtionObject = addtionObject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLoginVersion() {
        return loginVersion;
    }

    public void setLoginVersion(String loginVersion) {
        this.loginVersion = loginVersion;
    }
}
