package com.fontysio.colleaguetracker.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String coDomain = "iodigital.com";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + Pattern.quote(coDomain) + "$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    public boolean isValid(final String email) {
        return (validateEmail(email));
    }

    private boolean validateEmail(final String email) {
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}