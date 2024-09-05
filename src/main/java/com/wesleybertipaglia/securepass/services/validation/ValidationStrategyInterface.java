package com.wesleybertipaglia.securepass.services.validation;

import java.util.List;

public interface ValidationStrategyInterface {
    void validate(String password, List<String> suggestions);
}