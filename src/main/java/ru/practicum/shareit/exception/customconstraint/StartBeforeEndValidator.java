package ru.practicum.shareit.exception.customconstraint;

import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, CreateBookingRequest> {

    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateBookingRequest createBookingRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (createBookingRequest.getStart() == null || createBookingRequest.getEnd() == null)
            return false;
        return createBookingRequest.getStart().isBefore(createBookingRequest.getEnd());
    }
}