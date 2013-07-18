package com.odong.itpkg.form.admin;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午12:26
 */
public class SiteAboutMeForm implements Serializable {
    private static final long serialVersionUID = 7471810717220674719L;
    @NotNull
    private String aboutMe;

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}
