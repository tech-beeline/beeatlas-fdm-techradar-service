ALTER TABLE tech ADD COLUMN review boolean;
UPDATE tech SET review = true WHERE review IS NULL;