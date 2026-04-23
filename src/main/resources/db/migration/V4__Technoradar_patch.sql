UPDATE techradar.tech set deleted_date = current_date where label in
                                                            ('Rocky Linux 8.5',
                                                             'Rocky Linux <8.5',
                                                             'Ubuntu 20.04 22.04',
                                                             'Ubuntu < 20.04 22.04',
                                                             'ClickHouse 22.3.2.2',
                                                             'ClickHouse < 22.3.2.2',
                                                             'MongoDB 6.0.3',
                                                             'MongoDB < 6.0.3',
                                                             'PostgreSQL 14',
                                                             'PostgreSQL < 14',
                                                             'Redis 7.0',
                                                             'Redis < 7.0',
                                                             'Tarantool 2.11',
                                                             'Tarantool < 2.11',
                                                             'C# >=10',
                                                             'C# <10',
                                                             'Java 17',
                                                             'Java 8',
                                                             'Python 3.9',
                                                             'Python < 3.9');




INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Rocky Linux', 'Операционная система', 2, current_date, current_date, NULL,  'https://bwiki.beeline.ru/x/N-VsFg', 1);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Ubuntu', 'Операционная система',	2, current_date, current_date, NULL, 'https://bwiki.beeline.ru/x/M-VsFg', 1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'ClickHouse', 'Колоночная аналитическая СУБД', 	3, current_date, current_date, NULL, NULL, 1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'MongoDB', 'NoSQL СУБД', 3, current_date, current_date, NULL, 'https://bwiki.beeline.ru/x/EPVsFg'	,1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'PostgreSQL', 'Реляционная СУБД', 3, current_date, current_date, NULL, 'https://bwiki.beeline.ru/x/DvVsFg', 1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'Redis', 'NoSQL СУБД', 3, current_date, current_date, NULL, 'https://bwiki.beeline.ru/x/FPVsFg' ,1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'Tarantool', 'Платформа in-memory вычислений', 3, current_date, current_date, NULL, NULL,	1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'C#' , 'объектно-ориентированный язык программирования',4, current_date, current_date, NULL, 'https://bwiki.beeline.ru/x/CtOmFg'	,1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'Java', 'объектно-ориентированный язык программирования общего назначения',4, current_date, current_date, NULL, 'https://bwiki.beeline.ru/x/WvVsFg' ,1);

INSERT INTO techradar.tech (id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid)
VALUES (nextval('techradar.tech_id_seq'), 'Python','Высокоуровневый язык программирования общего назначения',4, current_date, current_date, NULL, 'https://bwiki.beeline.ru/x/PdOmFg' ,1);



INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'C shell (tcsh)', '', 2, current_date, current_date, NULL,  '', 4);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'HAProxy', '', 2, current_date, current_date, NULL,  '', 3);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Solaris', '', 2, current_date, current_date, NULL,  '', 4);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'GlassFish', '', 2, current_date, current_date, NULL,  '', 4);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Tuxedo Server', '', 2, current_date, current_date, NULL,  '', 4);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Veritas Cluster Server', '', 2, current_date, current_date, NULL,  '', 4);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Oracle client', '', 3, current_date, current_date, NULL,  '', 4);

INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Oracle EE', '', 3, current_date, current_date, NULL,  '', 4);


INSERT INTO techradar.tech
(id, label, description, sectorid, created_date, last_modified_date, deleted_date, link, ringid) VALUES
    (nextval('techradar.tech_id_seq'), 'Perl', '', 4, current_date, current_date, NULL,  '', 4);
