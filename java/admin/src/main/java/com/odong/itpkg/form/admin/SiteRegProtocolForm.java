package com.odong.itpkg.form.admin;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午12:26
 */
public class SiteRegProtocolForm implements Serializable {
    private static final long serialVersionUID = -7916687559100117429L;

    public String getRegProtocol() {
        return regProtocol;
    }

    public void setRegProtocol(String regProtocol) {
        this.regProtocol = regProtocol;
    }

    @NotNull

    private String regProtocol;
}
