ARG PGSQL_TS_SHARE_DIR=/usr/local/share/postgresql

FROM postgres:16.1-alpine3.19
ENV POSTGRES_DB=postgres
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=password

ADD ./init.sql /docker-entrypoint-initdb.d/
# Get from https://github.com/postgrespro/hunspell_dicts
COPY ru_ru.affix  /usr/local/share/postgresql/tsearch_data/ru_ru.affix
COPY ru_ru.dict   /usr/local/share/postgresql/tsearch_data/ru_ru.dict