package com.odong.itpkg.form.net.host;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 上午11:01
 */
public class RebootForm implements Serializable {
    private static final long serialVersionUID = -7893671902612868111L;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
