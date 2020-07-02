package com.mmanchola.blog.util;

import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class ServiceChecker {

  // Capitalize every word on String
  public String capitalizeFully(String string) {
    char[] chars = string.toLowerCase().toCharArray();
    boolean found = false;
    for (int i = 0; i < chars.length; i++) {
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i]);
        found = true;
      } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
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
        .map(gen -> gen.equals("MALE") || gen.equals("FEMALE") ? gen : "OTHER");
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

}
