package com.odong.portal.web.grid;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-24
 * Time: 下午4:53
 */
public class Cell implements Serializable {

    public Cell(String text, boolean html) {
        this.text = text;
        this.html = html;
    }

    private String text;
    private boolean html;
    private static final long serialVersionUID = -500272707265394014L;

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
