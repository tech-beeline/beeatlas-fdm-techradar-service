CREATE SEQUENCE techradar.seq_group_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE techradar.seq_patterns_group_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
	CACHE 1
	NO CYCLE;

CREATE TABLE techradar.group (
                                 id INTEGER PRIMARY KEY DEFAULT nextval('techradar.seq_group_id'),
                                 "name" varchar(50) NOT NULL,
                                 parent_id int4 NULL
);

CREATE TABLE techradar.pattern_group (
                                         id int4 PRIMARY KEY DEFAULT nextval('techradar.seq_patterns_group_id'::regclass),
                                         pattern_id int4 NOT NULL,
                                         group_id int4 NOT NULL
);

ALTER TABLE techradar.pattern_group ADD CONSTRAINT pattern_group_pattern_id_fkey FOREIGN KEY (pattern_id) REFERENCES techradar.pattern(id);
ALTER TABLE techradar.pattern_group ADD CONSTRAINT pattern_group_group_id_fkey FOREIGN KEY (group_id) REFERENCES techradar.group(id);

ALTER TABLE techradar.pattern ADD description text NULL;

