package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    private static final Pattern DNA_PATTERN = Pattern.compile("^[ATCG]+$");
    private static final int MAX_DNA_SIZE = 1000; // Maximum allowed matrix size

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null || dna.length == 0) {
            return false;
        }

        int n = dna.length;

        // Validation: maximum size check
        if (n > MAX_DNA_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "DNA exceeds maximum allowed size of " + MAX_DNA_SIZE + "x" + MAX_DNA_SIZE)
                    .addConstraintViolation();
            return false;
        }

        for (String row : dna) {
            if (row == null || row.length() != n) {
                return false;
            }
            if (!DNA_PATTERN.matcher(row.toUpperCase()).matches()) {
                return false;
            }
        }

        return true;
    }
}
