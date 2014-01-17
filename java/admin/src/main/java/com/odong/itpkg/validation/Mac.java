package com.odong.itpkg.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-26
 * Time: 下午4:31
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MacValidator.class)
@Documented
public @interface Mac {
    String message() default "{val.mac}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
