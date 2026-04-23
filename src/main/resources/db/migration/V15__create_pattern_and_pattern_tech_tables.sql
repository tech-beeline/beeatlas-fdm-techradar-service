CREATE SEQUENCE techradar.seq_patterns_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

CREATE SEQUENCE techradar.seq_patterns_tech_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

CREATE TABLE techradar.pattern
(
    id              integer NOT NULL DEFAULT nextval('techradar.seq_patterns_id'::regclass),
    code            text    NOT NULL,
    name            text    NOT NULL,
    rule            text,
    is_anti_pattern boolean NOT NULL DEFAULT false,
    create_date     timestamp without time zone,
    update_date     timestamp without time zone,
    delete_date     timestamp without time zone,
    CONSTRAINT pattern_pkey PRIMARY KEY (id),
    CONSTRAINT pattern_code_unique UNIQUE (code)
);

CREATE TABLE techradar.pattern_tech
(
    id         integer NOT NULL DEFAULT nextval('techradar.seq_patterns_tech_id'::regclass),
    pattern_id integer NOT NULL,
    tech_id    integer NOT NULL,
    CONSTRAINT pattern_tech_pkey PRIMARY KEY (id)
);

ALTER TABLE techradar.pattern_tech
    ADD CONSTRAINT pattern_tech_pattern_id_fkey
        FOREIGN KEY (pattern_id)
            REFERENCES techradar.pattern (id);

ALTER TABLE techradar.pattern_tech
    ADD CONSTRAINT pattern_tech_tech_id_fkey
        FOREIGN KEY (tech_id)
            REFERENCES techradar.tech (id);