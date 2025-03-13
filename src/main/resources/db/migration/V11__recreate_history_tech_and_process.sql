
DROP TABLE IF EXISTS history_tech CASCADE;
DROP TABLE IF EXISTS process CASCADE;
DROP TABLE IF EXISTS tech_bl_product CASCADE;
DROP TABLE IF EXISTS black_list CASCADE;

DROP SEQUENCE IF EXISTS seq_history_tech_id;
DROP SEQUENCE IF EXISTS seq_process_id;

CREATE SEQUENCE seq_history_tech_id INCREMENT 1 START 1;
CREATE SEQUENCE seq_process_id INCREMENT 1 START 1;


CREATE TABLE history_tech
(
    id integer NOT NULL DEFAULT NEXTVAL('seq_history_tech_id'::regclass),
    ref_id integer NOT NULL,
    label varchar(80) NOT NULL,
    description varchar(255) NOT NULL,
    sector_id integer NOT NULL,
    ring_id integer NOT NULL,
    version integer NOT NULL,
    created_date timestamp without time zone NOT NULL,
    link varchar(255) NULL
);

CREATE TABLE process
(
    id integer NOT NULL DEFAULT NEXTVAL('seq_process_id'::regclass),
    name_process varchar(100) NOT NULL,
    tech_id integer NULL,
    created_date timestamp without time zone NOT NULL,
    deleted_date timestamp without time zone NULL
);


ALTER TABLE history_tech ADD CONSTRAINT pk_history_tech PRIMARY KEY (id);
CREATE INDEX ixfk_history_tech_tech ON history_tech (ref_id ASC);

ALTER TABLE process ADD CONSTRAINT pk_process PRIMARY KEY (id);
CREATE INDEX ixfk_process_tech ON process (tech_id ASC);


ALTER TABLE history_tech ADD CONSTRAINT fk_history_tech_tech
    FOREIGN KEY (ref_id) REFERENCES tech (id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE process ADD CONSTRAINT fk_process_tech
    FOREIGN KEY (tech_id) REFERENCES tech (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
