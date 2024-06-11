-- public.simple_entity definition

-- Drop table

-- DROP TABLE public.simple_entity;
DROP TABLE IF EXISTS CLEARING_RECORD CASCADE;

CREATE TABLE if not exists CLEARING_RECORD (
	id int8 NOT NULL,
	transaction_date timestamp NOT NULL,
	posting_date timestamp NOT NULL,
	target_number varchar(255) NULL,
	trans_curr varchar(3) NULL,
	trans_amount numeric(20, 2) NOT NULL DEFAULT 0,
	comment_text text,
	attributes jsonb NULL,
	unmapped jsonb NULL,
	search_vector tsvector GENERATED ALWAYS AS (to_tsvector('russian', comment_text)) STORED
) PARTITION BY RANGE(transaction_date);

CREATE INDEX IF NOT EXISTS  ID_CLEARING_RECORD_IDX ON CLEARING_RECORD USING btree (id);
CREATE INDEX IF NOT EXISTS  TRANSACTION_DATE_CLEARING_RECORD_IDX ON CLEARING_RECORD USING btree (transaction_date);

CREATE TABLE IF NOT EXISTS CLEARING_RECORD_Y2023_M12_D13 PARTITION OF CLEARING_RECORD
    FOR VALUES FROM ('2023-12-13 00:00:00') TO ('2023-12-14 00:00:00');

CREATE TABLE IF NOT EXISTS CLEARING_RECORD_Y2023_M12_D14 PARTITION OF CLEARING_RECORD
    FOR VALUES FROM ('2023-12-14 00:00:00') TO ('2023-12-15 00:00:00');

insert into CLEARING_RECORD (id, transaction_date, posting_date, target_number, trans_curr, trans_amount, comment_text)
values (1, '2023-12-13 00:00:00', '2023-12-13 00:00:00', '123', 'USD', 100.00, 'test');


DROP TABLE IF EXISTS TRANSACTION_LOG CASCADE;

CREATE TABLE IF NOT EXISTS TRANSACTION_LOG (
    id uuid PRIMARY KEY default gen_random_uuid(),
    "content" jsonb NOT NULL
);


insert into TRANSACTION_LOG values(gen_random_uuid(), '{"a": 10, "b": 10}');
insert into TRANSACTION_LOG values(gen_random_uuid(), '{"a": 20, "b": 20}');
insert into TRANSACTION_LOG values(gen_random_uuid(), '{"a": 30, "b": 30}');


CREATE TABLE if not exists TRANSACTION_RECORD (
	id int8 PRIMARY KEY,
	transaction_date timestamp NOT NULL,
	posting_date timestamp NOT NULL,
	target_number varchar(255) NULL,
	trans_curr varchar(3) NULL,
	trans_amount numeric(20, 2) NOT NULL DEFAULT 0,
	comment_text text NULL
);