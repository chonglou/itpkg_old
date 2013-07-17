package com.odong.itpkg.entity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午8:03
 */
public class Setting implements Serializable {
    private static final long serialVersionUID = -313325086414886203L;
    private Long id;
    private String key;
    private String val;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
