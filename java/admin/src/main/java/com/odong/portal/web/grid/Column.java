package com.odong.portal.web.grid;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:27
 */
public class Column implements Serializable {
    public Column(String label) {
        this(label, null);
    }

    public Column(String label, String width) {
        this.label = label;
        this.width = width;
    }

    private static final long serialVersionUID = 3809918329301775689L;
    private String label;
    private String width;
    private boolean html;

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
