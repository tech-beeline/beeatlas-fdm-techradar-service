
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM techradar.techcategory) THEN
    RAISE NOTICE 'Skip V6__techradar_techcategory: techradar.techcategory is not empty';
    RETURN;
END IF;

INSERT INTO techradar.techcategory (id, techid, categoryid)
SELECT
    nextval('techradar.techcategory_id_seq') AS id,
    t.id AS techid,
    c.id AS categoryid
FROM (
         VALUES
             ('Rocky Linux', 'Инфраструктура'),
             ('Rocky Linux', 'Devops'),
             ('Rocky Linux', 'Безопасность'),
             ('Ubuntu', 'Инфраструктура'),
             ('Ubuntu', 'Devops'),
             ('Ubuntu', 'Безопасность'),
             ('Solaris', 'Инфраструктура'),
             ('Solaris', 'Devops'),
             ('Solaris', 'Безопасность'),
             ('C shell (tcsh)', 'Инфраструктура'),
             ('C shell (tcsh)', 'Devops'),
             ('C shell (tcsh)', 'Безопасность'),
             ('Veritas Cluster Server', 'Инфраструктура'),
             ('Veritas Cluster Server', 'Безопасность'),
             ('Veritas Cluster Server', 'Мониторинг и трассировка'),
             ('ClickHouse', 'Хранение данных'),
             ('ClickHouse', 'Интеграция'),
             ('ClickHouse', 'Мониторинг и трассировка'),
             ('MongoDB', 'Хранение данных'),
             ('MongoDB', 'Интеграция'),
             ('MongoDB', 'Мониторинг и трассировка'),
             ('PostgreSQL', 'Хранение данных'),
             ('PostgreSQL', 'Интеграция'),
             ('PostgreSQL', 'Мониторинг и трассировка'),
             ('PostgreSQL', 'Безопасность'),
             ('Redis', 'Хранение данных'),
             ('Redis', 'Интеграция'),
             ('Redis', 'Мониторинг и трассировка'),
             ('Redis', 'Безопасность'),
             ('Tarantool', 'Хранение данных'),
             ('Tarantool', 'Интеграция'),
             ('Tarantool', 'Мониторинг и трассировка'),
             ('Tarantool', 'Безопасность'),
             ('C#', 'Проектирование'),
             ('C#', 'Тестирование'),
             ('C#', 'Безопасность'),
             ('Java', 'Проектирование'),
             ('Java', 'Тестирование'),
             ('Java', 'Безопасность'),
             ('Python', 'Проектирование'),
             ('Python', 'Тестирование'),
             ('Python', 'Безопасность'),
             ('Python', 'Devops'),
             ('Perl', 'Проектирование'),
             ('Perl', 'Тестирование'),
             ('Perl', 'Безопасность'),
             ('GlassFish', 'Инфраструктура'),
             ('GlassFish', 'Проектирование'),
             ('GlassFish', 'Интеграция'),
             ('GlassFish', 'Безопасность'),
             ('GlassFish', 'Мониторинг и трассировка'),
             ('Tuxedo Server', 'Инфраструктура'),
             ('Tuxedo Server', 'Проектирование'),
             ('Tuxedo Server', 'Интеграция'),
             ('Tuxedo Server', 'Безопасность'),
             ('Tuxedo Server', 'Мониторинг и трассировка'),
             ('Oracle client', 'Инфраструктура'),
             ('Oracle client', 'Интеграция'),
             ('Oracle client', 'Безопасность'),
             ('Oracle client', 'Мониторинг и трассировка'),
             ('Oracle EE', 'Инфраструктура'),
             ('Oracle EE', 'Интеграция'),
             ('Oracle EE', 'Безопасность'),
             ('Oracle EE', 'Мониторинг и трассировка'),
             ('HAProxy', 'Инфраструктура'),
             ('HAProxy', 'Интеграция'),
             ('HAProxy', 'Безопасность'),
             ('HAProxy', 'Мониторинг и трассировка')
     ) AS m(tech_label, category_name)
         JOIN techradar.tech t
              ON t.label = m.tech_label
         LEFT JOIN techradar.category c
                   ON c.name = m.category_name
WHERE
    c.id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM techradar.techcategory tc
    WHERE tc.techid = t.id
      AND tc.categoryid = c.id
);

PERFORM setval(
    'techradar.techcategory_id_seq'::regclass,
    (SELECT COALESCE(MAX(id), 0) FROM techradar.techcategory),
    true
  );
END $$;

