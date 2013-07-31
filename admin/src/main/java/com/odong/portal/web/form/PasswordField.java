package com.odong.portal.web.form;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:42
 */
public final class PasswordField extends Field<String> {
    private static final long serialVersionUID = 4601649521987736108L;
    private int width;

    public PasswordField(String id, String label) {
        this(id, label, null);
    }

    public PasswordField(String id, String label, String tooltip) {
        super(id, label, "password", null, true, tooltip);
        this.width = 300;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
