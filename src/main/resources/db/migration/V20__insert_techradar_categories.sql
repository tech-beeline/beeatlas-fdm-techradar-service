DO
$$
BEGIN
  IF
EXISTS (SELECT 1 FROM techradar.category) THEN
    RAISE NOTICE 'Skip V5__techradar_category: techradar.category is not empty';
    RETURN;
END IF;

INSERT INTO techradar.category (id, name)
VALUES (2, 'Брокеры сообщений'),
       (3, 'Инфраструктура'),
       (4, 'Дизайн'),
       (6, 'Проектирование'),
       (7, 'Тестирование'),
       (8, 'Безопасность'),
       (9, 'Mobile (Android)'),
       (10, 'Хранение данных'),
       (11, 'Интеграция'),
       (12, 'Корпоративные коммуникации'),
       (13, 'Мониторинг и трассировка'),
       (17, 'Frontend'),
       (18, 'Devops') ON CONFLICT (id) DO
UPDATE
    SET name = EXCLUDED.name;

PERFORM
setval(
    'techradar.category_id_seq'::regclass,
    (SELECT COALESCE(MAX(id), 0) FROM techradar.category),
    true
  );
END $$;
