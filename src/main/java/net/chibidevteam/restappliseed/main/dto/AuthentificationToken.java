package net.chibidevteam.restappliseed.main.dto;

import java.io.Serializable;

import net.chibidevteam.utils.helper.ClassHelper;

public class AuthentificationToken implements Serializable {

    private static final long serialVersionUID = 5713349463606576719L;
    private String            token;

    public AuthentificationToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int hashCode() {
        return ClassHelper.hash(AuthentificationToken.class, this);
    }

    @Override
    public boolean equals(Object obj) {
        return ClassHelper.areEquals(AuthentificationToken.class, this, obj);
    }

}
