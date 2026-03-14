package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartDateInFutureValidator.class)
@Documented
public @interface StartDateInFuture {

	String message() default "Ngày bắt đầu khóa học phải là hôm nay hoặc trong tương lai";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
