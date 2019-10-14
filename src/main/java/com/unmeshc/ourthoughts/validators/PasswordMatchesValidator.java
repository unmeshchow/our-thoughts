package com.unmeshc.ourthoughts.validators;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by uc on 9/30/2019
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        String password =
                new BeanWrapperImpl(object).getPropertyValue("password").toString();
        String matchingPassword =
                new BeanWrapperImpl(object).getPropertyValue("matchingPassword").toString();

        return password.equals(matchingPassword);
    }
}
