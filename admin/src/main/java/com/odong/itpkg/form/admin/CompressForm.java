package com.odong.itpkg.form.admin;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午12:16
 */
public class CompressForm implements Serializable {
    private static final long serialVersionUID = -827966361274260094L;
    private int days;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
