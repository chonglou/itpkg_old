package com.odong.portal.web.form;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午2:27
 */
public class AgreeField extends Field<Boolean> {
    private static final long serialVersionUID = 2572220499336454266L;

    public AgreeField(String id, String label, String text) {
        super(id, label, "agree", false, true, null);
        this.html = true;
        this.text = text;
    }

    private boolean html;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}
