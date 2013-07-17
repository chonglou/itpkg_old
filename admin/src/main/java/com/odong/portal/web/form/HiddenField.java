package com.odong.portal.web.form;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:38
 */
public final class HiddenField<T> extends Field<T> {
    private static final long serialVersionUID = -441623698916913644L;

    public HiddenField(String id, T value) {
        super(id, null, "hidden", value, false, null);
    }
}
