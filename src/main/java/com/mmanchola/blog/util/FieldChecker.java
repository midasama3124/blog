package com.mmanchola.blog.util;

import com.mmanchola.blog.model.PostStatus;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.mmanchola.blog.model.PersonGender.*;

@Component
public class FieldChecker {

    private UrlValidator urlValidator;

    public FieldChecker() {
        this.urlValidator = new UrlValidator();
    }

    // Capitalize every word on String
    public String capitalizeFully(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    // Check given string is not empty (Not-null attribute)
    public Optional<String> checkNotEmpty(String str) {
        return Optional.ofNullable(str)
                .filter(Predicate.not(String::isEmpty));
    }

    // Check given person name
    public Optional<String> checkPersonName(String name) {
        return Optional.ofNullable(name)
                .filter(Predicate.not(String::isEmpty))
                .map(this::capitalizeFully);
    }

    // Check given person gender
    public Optional<String> checkGender(String gender) {
        return Optional.ofNullable(gender)
                .filter(Predicate.not(String::isEmpty))
                .map(String::toUpperCase)
                .map(gen -> gen.equals(MALE.toString()) || gen.equals(FEMALE.toString()) ? gen : OTHER.toString());
    }

    // Check given person age
    public Optional<Short> checkAge(short age) {
        return Optional.of(age)
                .filter(a -> a > 0);
    }

    // Check given role name
    public Optional<String> checkRoleName(String role) {
        return Optional.ofNullable(role)
                .filter(Predicate.not(String::isEmpty))
                .map(String::toUpperCase);
    }

    // Check given slug in terms of correctness
    public Optional<String> checkSlugCorrectness(String slug) {
        return Optional.ofNullable(slug)
                .filter(Predicate.not(String::isEmpty))
                .map(s -> StringUtils.replace(s, " ", "-"));  // Replace blank spaces for dashes
    }

    // Check given slug in terms of availability for POST operations (Not-null attribute)
    public Optional<String> checkSlugAvailability(String slug, Predicate<? super String> isTaken) {
        return Optional.ofNullable(slug)
                .filter(Predicate.not(isTaken));   // Check availability
    }

    // Check given slug in terms of availability for PUT operations (Not-null attribute)
    public Optional<String> checkSlugAvailability(String slug, int id, BiPredicate<? super String, ? super Integer> isTakenByOther) {
        return Optional.ofNullable(slug)
                .filter(sl -> !isTakenByOther.test(sl, id));   // Check availability
    }

    // Check given status
    public Optional<String> checkStatus(String status) {
        return Optional.ofNullable(status)
                .filter(Predicate.not(String::isEmpty))
                .map(String::toLowerCase)
                .filter(PostStatus::contains);    // Must be contained in PostStatus enum
    }

    public Optional<String> checkUrl(String url) {
        if (url != null && url.isEmpty())
            return Optional.of(url);
        return Optional.ofNullable(url)
                .filter(urlValidator::isValid);
    }

}
