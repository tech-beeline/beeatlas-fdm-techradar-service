DROP SEQUENCE IF EXISTS seq_history_tech_id;


DROP TABLE IF EXISTS history_tech;


CREATE SEQUENCE seq_history_tech_id INCREMENT 1 START 1;


CREATE TABLE history_tech
(
    id integer NOT NULL DEFAULT NEXTVAL(('seq_history_tech_id'::text)::regclass),
    ref_id integer NOT NULL,
    label varchar(80) NOT NULL,
    description varchar(255) NOT NULL,
    sector_id integer NOT NULL,
    ring_id integer NOT NULL,
    version integer NOT NULL,
    created_date timestamp without time zone NOT NULL,
    link varchar(255) NULL
);

ALTER TABLE history_tech ADD CONSTRAINT pk_history_tech PRIMARY KEY (id);

CREATE INDEX ixfk_history_tech_tech ON history_tech (ref_id ASC);

ALTER TABLE history_tech ADD CONSTRAINT fk_history_tech_tech FOREIGN KEY (ref_id) REFERENCES tech (id) ON DELETE No Action ON UPDATE No Action;
ALTER TABLE history_tech ADD CONSTRAINT fk_history_tech_ring FOREIGN KEY (ring_id) REFERENCES ring (id) ON DELETE No Action ON UPDATE No Action;
ALTER TABLE history_tech ADD CONSTRAINT fk_history_tech_sector FOREIGN KEY (sector_id) REFERENCES sector (id) ON DELETE No Action ON UPDATE No Action;
