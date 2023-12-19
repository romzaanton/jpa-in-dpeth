ARG PGSQL_TS_SHARE_DIR=/usr/local/share/postgresql

FROM postgres:16.1-alpine3.19
COPY ru_ru.affix  /usr/local/share/postgresql/tsearch_data/ru_ru.affix
COPY ru_ru.dict   /usr/local/share/postgresql/tsearch_data/ru_ru.dict