package com.fontysio.colleaguetracker.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String coDomain = "iodigital.com";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + Pattern.quote(coDomain) + "$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);
    private static final ArrayList<String> authorizedEmails = new ArrayList<>(Arrays.asList("colleaguetracker@gmail.com", "ik@josian.nl", "nielsfeijen76@gmail.com","oggivseerden@gmail.com", "abevdwe@gmail.com"));

    public static boolean isValid(final String email) {
        for (String authorizedEmail:authorizedEmails) {
            if (email.equals(authorizedEmail)) {
                return true;
            }
        }
        return (validateEmail(email));
    }

    private static boolean validateEmail(final String email) {

        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}