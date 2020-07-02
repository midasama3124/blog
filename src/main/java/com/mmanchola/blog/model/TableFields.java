package com.mmanchola.blog.model;

public enum TableFields {
  PERSON_ID,
  PERSON_FIRST_NAME,
  PERSON_LAST_NAME,
  PERSON_GENDER,
  PERSON_AGE,
  PERSON_EMAIL,
  PERSON_PASSWORD,
  PERSON_REGISTERED_AT,
  PERSON_LAST_LOGIN,
  POST_ID,
  POST_PERSON_ID,
  POST_PARENT_ID,
  POST_PARENT_PATH,
  POST_TITLE,
  POST_METATITLE,
  POST_SLUG,
  POST_STATUS,
  POST_PUBLISHED_AT,
  POST_UPDATED_AT,
  POST_CONTENT,
  ROLE_ID,
  ROLE_NAME,
  ROLE_DESCRIPTION;

  @Override
  public String toString() {
    return super
        .toString()
        .toLowerCase()
        .replace('_', ' ');
  }
}
