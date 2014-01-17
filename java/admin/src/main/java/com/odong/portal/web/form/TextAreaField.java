package com.odong.portal.web.form;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:40
 */
public final class TextAreaField extends Field<String> {
    private static final long serialVersionUID = 2811328119954932042L;

    public TextAreaField(String id, String label) {
        this(id, label, null, null);
    }

    public TextAreaField(String id, String label, String value) {
        this(id, label, value, null);
    }

    public TextAreaField(String id, String label, String value, String tooltip) {
        super(id, label, "textarea", value, false, tooltip);
        this.width = 600;
        this.height = 350;
        this.html = true;
    }

    private boolean html;
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}
