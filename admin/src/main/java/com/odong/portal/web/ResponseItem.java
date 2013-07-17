package com.odong.portal.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 上午12:04
 */
public class ResponseItem implements Serializable {

    public ResponseItem(Type type) {
        this.ok = false;
        this.type = type;
        this.created = new Date();
        this.data = new ArrayList<>();
    }

    public void addData(Object o) {
        this.data.add(o);
    }

    public enum Type {
        list, form, grid, chart, message, redirect
    }

    private static final long serialVersionUID = 2746215318151397733L;
    private Date created;
    private boolean ok;
    private Type type;
    private List<Object> data;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
