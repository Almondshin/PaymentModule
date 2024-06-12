package com.modules;

import com.modules.link.controller.dto.AgencyDtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class RegisterInfoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNotNullFields() {
        RegisterInfo info = new RegisterInfo();
        // 설정하지 않은 필드가 많기 때문에 검증에서 실패해야 함
        Set<ConstraintViolation<RegisterInfo>> violations = validator.validate(info);

        assertFalse(violations.isEmpty());

        violations.forEach(violation -> {
            System.out.println("Violation: " + violation.getPropertyPath() + " - " + violation.getMessage());
        });
    }
}
