package com.odong.itpkg.form.admin;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-28
 * Time: 下午12:38
 */
public class CaptchaForm implements Serializable {
    private static final long serialVersionUID = 717290162470610009L;
    private String mode;
    private int width;
    private int height;
    private String chars;
    private int length;

    private String privateKey;
    private String publicKey;
    private boolean includeNoScript;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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

    public boolean isIncludeNoScript() {
        return includeNoScript;
    }

    public void setIncludeNoScript(boolean includeNoScript) {
        this.includeNoScript = includeNoScript;
    }

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

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
