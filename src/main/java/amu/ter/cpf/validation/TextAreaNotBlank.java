package amu.ter.cpf.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;


import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = TextAreaValidator.class)
public @interface TextAreaNotBlank {
    String message() default "Ce champ est obligatoire";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
