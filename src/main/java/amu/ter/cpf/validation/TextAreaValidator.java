package amu.ter.cpf.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TextAreaValidator implements ConstraintValidator<TextAreaNotBlank, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return false;
        String text = value.replaceAll("<[^>]*>", "").trim();
        return !text.isEmpty();
    }
}
