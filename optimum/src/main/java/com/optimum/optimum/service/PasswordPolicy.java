package com.optimum.optimum.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {
    public List<String> violations(String password) {
        List<String> errors = new ArrayList<>();
        if (password == null || password.length() < 8) {
            errors.add("Le mot de passe doit contenir au moins 8 caracteres.");
        }
        if (password == null || !password.matches(".*[A-Z].*")) {
            errors.add("Le mot de passe doit contenir une majuscule.");
        }
        if (password == null || !password.matches(".*[a-z].*")) {
            errors.add("Le mot de passe doit contenir une minuscule.");
        }
        if (password == null || !password.matches(".*\\d.*")) {
            errors.add("Le mot de passe doit contenir un chiffre.");
        }
        if (password == null || !password.matches(".*[^A-Za-z0-9].*")) {
            errors.add("Le mot de passe doit contenir un caractere special.");
        }
        return errors;
    }

    public void validate(String password) {
        List<String> errors = violations(password);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errors));
        }
    }
}
