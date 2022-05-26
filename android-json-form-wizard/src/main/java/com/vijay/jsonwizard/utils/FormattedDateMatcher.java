package com.vijay.jsonwizard.utils;

import java.util.regex.Pattern;

public class FormattedDateMatcher  {

    private static Pattern DATE_PATTERN = Pattern.compile(
      "^\\d{2}-\\d{2}-\\d{4}$");

    private static Pattern BS_DATE_PATTERN = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}$");


    public static boolean matches(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }

    public static boolean matchesBSdate(String date) {
        return BS_DATE_PATTERN.matcher(date).matches();
    }
}
