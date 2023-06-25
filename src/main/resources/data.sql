MERGE INTO genre KEY (id) VALUES (1, 'Комедия'), (2, 'Драма'),
	 (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

MERGE INTO mpa KEY (id) VALUES (1, 'G'), (2, 'PG'),
	 (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

MERGE INTO friendship_status KEY (id) VALUES (1, 'подтвержденный'), (2, 'неподтвержденный');