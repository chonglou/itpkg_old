package com.odong.itpkg.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-26
 * Time: 下午4:42
 */
public class MacValidator implements ConstraintValidator<Mac, String> {
    @Override
    public void initialize(Mac mac) {
        //
    }

    @Override
    public boolean isValid(String mac, ConstraintValidatorContext constraintValidatorContext) {
        if (mac == null) {
            return false;
        }
        String[] ss = mac.split(":");
        if (ss.length != 6) {
            return false;
        }
        try {
            for (String s : ss) {
                int i = Integer.parseInt(s, 16);
                System.out.println("###### " + i);
                if (i < 0 || i > 0xff) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;  //
    }
}
