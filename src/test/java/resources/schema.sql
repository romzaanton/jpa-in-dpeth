CREATE TABLE if not exists CLEARING_RECORD (
	id int8 NOT NULL,
	transaction_date timestamp NOT NULL,
	posting_date timestamp NOT NULL,
	target_number varchar(255) NULL,
	trans_curr varchar(3) NULL,
	trans_amount numeric(20, 2) NOT NULL DEFAULT 0,
	comment_text text NULL,
	attributes jsonb NULL,
	unmapped jsonb NULL
) PARTITION BY RANGE(transaction_date);


CREATE TABLE IF NOT EXISTS TRANSACTION_LOG (
    id uuid PRIMARY KEY default gen_random_uuid(),
    "content" jsonb NOT NULL
);


CREATE TABLE if not exists TRANSACTION_RECORD (
	id int8 PRIMARY KEY,
	transaction_date timestamp NOT NULL,
	posting_date timestamp NOT NULL,
	target_number varchar(255) NULL,
	trans_curr varchar(3) NULL,
	trans_amount numeric(20, 2) NOT NULL DEFAULT 0,
	comment_text text NULL
);

CREATE TABLE IF NOT EXISTS TRANSACTION_LOG (
    id uuid PRIMARY KEY default gen_random_uuid(),
    "content" jsonb NOT NULL
);
