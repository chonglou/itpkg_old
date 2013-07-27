package com.odong.itpkg.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-26
 * Time: 下午4:32
 */
public class IpV4Validator implements ConstraintValidator<IpV4, String> {
    @Override
    public void initialize(IpV4 ipV4) {
        //
    }

    @Override
    public boolean isValid(String ip, ConstraintValidatorContext constraintValidatorContext) {
        if (ip == null) {
            return false;
        }
        String[] ss = ip.split("\\.");
        if (ss.length != 4) {
            return false;
        }

        try {
            for (String s : ss) {
                int i = Integer.parseInt(s);
                if (i < 0 && i > 255) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;  //
    }
}
