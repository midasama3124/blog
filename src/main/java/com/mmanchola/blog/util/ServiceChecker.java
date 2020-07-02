package com.mmanchola.blog.util;

import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ServiceChecker {

  // Check given string is not empty (Not-null attribute)
  public Optional<String> checkNotEmpty(String str) {
    return Optional.ofNullable(str)
        .filter(Predicate.not(String::isEmpty));
  }

  // Check given person name
  public Optional<String> checkPersonName(String name) {
    return Optional.ofNullable(name)
        .filter(Predicate.not(String::isEmpty))
        .map(StringUtils::capitalize);
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
