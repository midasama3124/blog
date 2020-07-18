package com.mmanchola.blog.model;

public enum TableFields {
    PERSON_ID("El ID del usuario"),
    PERSON_FIRST_NAME("El nombre del usuario"),
    PERSON_LAST_NAME("El apellido del usuario"),
    PERSON_GENDER("El género del usuario"),
    PERSON_AGE("La edad del usuario"),
    PERSON_EMAIL("El email"),
    PERSON_PASSWORD("La contraseña del usuario"),
    PERSON_REGISTERED_AT("La fecha de registro del usuario"),
    PERSON_LAST_LOGIN("La fecha de último ingreso del usuario"),
    POST_ID("El ID del post"),
    POST_PERSON_ID("El ID del autor del post"),
    POST_PARENT_ID("El ID del padre del post"),
    POST_PARENT_PATH("La ruta de herencia del post"),
    POST_TITLE("El título del post"),
    POST_METATITLE("El metatítulo del post"),
    POST_SLUG("El URL del post"),
    POST_STATUS("El estado del post"),
    POST_PUBLISHED_AT("La fecha de publicación del post"),
    POST_UPDATED_AT("La fecha de actualización del post"),
    POST_CONTENT("El contenido del post"),
    ROLE_ID("El ID del rol"),
    ROLE_NAME("El nombre del rol"),
    ROLE_DESCRIPTION("La descripción del rol"),
    TAG_ID("El ID del tag"),
    TAG_TITLE("El título del tag"),
    TAG_METATITLE("El metatítulo del tag"),
    TAG_SLUG("El URL del tag"),
    TAG_CONTENT("El contenido del tag"),
    CATEGORY_ID("El ID de la categoría"),
    CATEGORY_PARENT_ID("El ID del padre de la categoría"),
    CATEGORY_TITLE("El título de la categoría"),
    CATEGORY_METATITLE("El metatítulo de la categoría"),
    CATEGORY_SLUG("El URL de la categoría"),
    CATEGORY_CONTENT("El contenido de la categoría");

    private String name;

    TableFields(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
