CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

/* Person/User table */
CREATE TABLE person (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    gender VARCHAR(10),
    age SMALLINT,
    email VARCHAR(50),
    password_hash VARCHAR(255) NOT NULL,
    registered_at TIMESTAMP NOT NULL,
    last_login TIMESTAMP NULL DEFAULT NULL,
    UNIQUE (email),
    CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$') );

/* Role table */
CREATE TABLE role (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100),
    UNIQUE (name) );

/* Permission table */
CREATE TABLE permission (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    UNIQUE (name) );

/* Person/Role table */
CREATE TABLE person_role (
    person_id uuid NOT NULL,
    role_id SMALLINT NOT NULL,
    PRIMARY KEY (person_id, role_id),
    FOREIGN KEY (person_id) REFERENCES person(id),
    FOREIGN KEY (role_id) REFERENCES role(id) );

/* Role/Permission table */
CREATE TABLE role_permission (
    role_id SMALLINT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id) );

/* Post table */
CREATE EXTENSION IF NOT EXISTS ltree;

/* Parent ID solution */
/* Use a hybrid of our original parent_id (for the referential consistency and 
simplicity of the child/parent relationship) and our ltree paths (for improved 
querying power/indexing). */
CREATE TABLE post (
    id SERIAL PRIMARY KEY,
    person_id uuid NOT NULL,
    parent_id INT REFERENCES post,
    parent_path LTREE,
    title VARCHAR(75) NOT NULL,
    metatitle VARCHAR(100),
    slug VARCHAR(100) NOT NULL,
    status VARCHAR(10) NOT NULL,
    published_at TIMESTAMP,
    updated_at TIMESTAMP,
    content TEXT,
    UNIQUE (slug),
    FOREIGN KEY (person_id) REFERENCES person (id),
    CONSTRAINT proper_status CHECK (status IN ('draft', 'published', 'outdated')) );

CREATE INDEX post_parent_path_idx ON post USING GIST (parent_path);
CREATE INDEX post_parent_id_idx ON post (parent_id);

/* Hide the management of the ltree path behind a trigger and only ever update 
the parent_id column */
CREATE OR REPLACE FUNCTION update_post_parent_path() RETURNS TRIGGER AS $$
    DECLARE
        path ltree;
    BEGIN
        IF NEW.parent_id IS NULL THEN
            NEW.parent_path = 'root'::ltree;
        ELSEIF TG_OP = 'INSERT' OR OLD.parent_id IS NULL OR OLD.parent_id != NEW.parent_id THEN
            SELECT parent_path || id::text FROM post WHERE id = NEW.parent_id INTO path;
            IF path IS NULL THEN
                RAISE EXCEPTION 'Invalid parent_id %', NEW.parent_id;
            END IF;
            NEW.parent_path = path;
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER post_parent_path_tgr
    BEFORE INSERT OR UPDATE ON post
    FOR EACH ROW EXECUTE PROCEDURE update_post_parent_path();

/* Comment table */
CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    published_at TIMESTAMP NOT NULL,
    content VARCHAR(500) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post (id) );

/* Category table */
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    parent_id INT REFERENCES category,
    parent_path LTREE,
    title VARCHAR(75) NOT NULL,
    metatitle VARCHAR(100),
    slug VARCHAR(100) NOT NULL,
    content TEXT );

CREATE INDEX category_parent_path_idx ON category USING GIST (parent_path);
CREATE INDEX category_parent_id_idx ON category (parent_id);

CREATE OR REPLACE FUNCTION update_category_parent_path() RETURNS TRIGGER AS $$
    DECLARE
        path ltree;
    BEGIN
        IF NEW.parent_id IS NULL THEN
            NEW.parent_path = 'root'::ltree;
        ELSEIF TG_OP = 'INSERT' OR OLD.parent_id IS NULL OR OLD.parent_id != NEW.parent_id THEN
            SELECT parent_path || id::text FROM category WHERE id = NEW.parent_id INTO path;
            IF path IS NULL THEN
                RAISE EXCEPTION 'Invalid parent_id %', NEW.parent_id;
            END IF;
            NEW.parent_path = path;
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER category_parent_path_tgr
    BEFORE INSERT OR UPDATE ON category
    FOR EACH ROW EXECUTE PROCEDURE update_category_parent_path();

/* Post/Category table */
CREATE TABLE post_category (
    post_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (post_id, category_id),
    FOREIGN KEY (post_id) REFERENCES post(id),
    FOREIGN KEY (category_id) REFERENCES category(id) );

/* Tag table */
CREATE TABLE tag (
    id SERIAL PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    metatitle VARCHAR(75),
    slug VARCHAR(75) NOT NULL,
    content TEXT );

/* Post/Tag table */
CREATE TABLE post_tag (
    post_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES post(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id) );

/* Image table */
CREATE TABLE image (
    id SERIAL PRIMARY KEY,
    filepath TEXT NOT NULL,
    caption VARCHAR(200) );

/* Post/Image table */
CREATE TABLE post_image (
    post_id INT NOT NULL,
    image_id INT NOT NULL,
    PRIMARY KEY (post_id, image_id),
    FOREIGN KEY (post_id) REFERENCES post(id),
    FOREIGN KEY (image_id) REFERENCES image(id) );