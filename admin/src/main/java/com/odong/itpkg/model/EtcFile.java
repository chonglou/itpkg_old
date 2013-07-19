package com.odong.itpkg.model;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午9:10
 */
public class EtcFile {

    public EtcFile(String name, String owner, String mode, String data) {
        this.name = name;
        this.owner = owner;
        this.mode = mode;
        this.data = data;
    }


    private String name;
    private String owner;
    private String mode;
    private String data;

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public String getData() {
        return data;
    }
}
