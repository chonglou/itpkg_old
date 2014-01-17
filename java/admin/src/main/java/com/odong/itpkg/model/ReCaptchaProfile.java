package com.odong.itpkg.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-27
 * Time: 下午9:09
 */
public class ReCaptchaProfile implements Serializable {
    private static final long serialVersionUID = -7644877320109268083L;
    private String privateKey;
    private String publicKey;
    private boolean includeNoScript;

    public boolean isIncludeNoScript() {
        return includeNoScript;
    }

    public void setIncludeNoScript(boolean includeNoScript) {
        this.includeNoScript = includeNoScript;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
