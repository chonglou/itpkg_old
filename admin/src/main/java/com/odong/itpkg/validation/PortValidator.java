package com.odong.itpkg.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-26
 * Time: 下午4:36
 */
public class PortValidator implements ConstraintValidator<Port, Integer> {

    @Override
    public void initialize(Port port) {
    }

    @Override
    public boolean isValid(Integer port, ConstraintValidatorContext constraintValidatorContext) {
        return port != null && port > 0 && port < 65536;
    }
}
