package com.wesleybertipaglia.securepass.services.validation;

import java.util.List;

public interface ValidationStrategy {
    void validate(String password, List<String> suggestions);
}