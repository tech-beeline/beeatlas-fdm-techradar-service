INSERT INTO Sector (id, name, "order") VALUES
                                           (1, 'Фреймворки и инструменты', 0),
                                           (2, 'Платформа и инфраструктура', 1),
                                           (3, 'Управление данными', 2),
                                           (4, 'Языки', 3)
    ON CONFLICT (id) DO UPDATE
                            SET name = EXCLUDED.name,
                            "order" = EXCLUDED."order";


INSERT INTO ring (id, name, "order") VALUES
                                           (1, 'Adopt', 0),
                                           (2, 'Trial', 1),
                                           (3, 'Assess', 2),
                                           (4, 'Hold', 3)
    ON CONFLICT (id) DO UPDATE
                            SET name = EXCLUDED.name,
                            "order" = EXCLUDED."order";

/* Drop Sequences */

DROP SEQUENCE  IF EXISTS  tech_id_seq  CASCADE
;

/* Create Sequences */

CREATE SEQUENCE tech_id_seq
    INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1
;
;
DROP SEQUENCE  IF EXISTS  techcategory_id_seq  CASCADE
;

/* Create Sequences */

CREATE SEQUENCE techcategory_id_seq
    INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1
;
;

DROP SEQUENCE  IF EXISTS  category_id_seq  CASCADE
;

/* Create Sequences */

CREATE SEQUENCE category_id_seq
    INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1
;
;

ALTER TABLE Tech
ALTER COLUMN link TYPE varchar(1000);