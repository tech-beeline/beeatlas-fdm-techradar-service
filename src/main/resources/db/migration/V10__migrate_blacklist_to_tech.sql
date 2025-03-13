
INSERT INTO techradar.ring (id, name, "order") VALUES (0, 'без статуса', 4) ON CONFLICT (id) DO NOTHING;
INSERT INTO techradar.sector (id, name, "order") VALUES (0, 'без сектора', 4) ON CONFLICT (id) DO NOTHING;

SELECT setval('tech_id_seq', COALESCE((SELECT MAX(id) + 1 FROM techradar.tech), 1));
ALTER TABLE techradar.tech ALTER COLUMN id SET DEFAULT nextval('tech_id_seq'::regclass);

INSERT INTO techradar.tech ("label", description, sectorid, created_date, last_modified_date, deleted_date, link, ringid, review)
SELECT src."label", '', 0, src.create_date, src.create_date, now(), null, 0, src.review
FROM techradar.black_list AS src
    ON CONFLICT DO NOTHING;
