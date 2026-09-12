package com.epam.rd.autocode.assessment.appliances.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private final MessageSource messageSource;

    private int minLength;
    private int maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecial;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecial = constraintAnnotation.requireSpecial();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true;
        }

        Locale locale = LocaleContextHolder.getLocale();

        // Довжина
        if (password.length() < minLength || password.length() > maxLength) {
            addViolation(context, "password.strong.length",
                    new Object[]{minLength, maxLength}, locale);
            return false;
        }

        // Великі літери
        if (requireUppercase && !password.chars().anyMatch(Character::isUpperCase)) {
            addViolation(context, "password.strong.uppercase", null, locale);
            return false;
        }

        // Малі літери
        if (requireLowercase && !password.chars().anyMatch(Character::isLowerCase)) {
            addViolation(context, "password.strong.lowercase", null, locale);
            return false;
        }

        // Цифри
        if (requireDigit && !password.chars().anyMatch(Character::isDigit)) {
            addViolation(context, "password.strong.digit", null, locale);
            return false;
        }

        // Спеціальні символи
        if (requireSpecial && !password.chars().anyMatch(ch ->
                "!@#$%^&*()_+-=[]{}|;:',.<>?/~`".indexOf(ch) >= 0)) {
            addViolation(context, "password.strong.special", null, locale);
            return false;
        }

        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String messageKey,
                              Object[] args, Locale locale) {
        String message = messageSource.getMessage(messageKey, args, locale);

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}