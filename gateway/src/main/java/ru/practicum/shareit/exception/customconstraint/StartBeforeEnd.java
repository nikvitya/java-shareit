package ru.practicum.shareit.exception.customconstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StartBeforeEndValidator.class})
public @interface StartBeforeEnd {
    String message() default "Время начала использования вещи должно быть строго раньше времени окончания.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}