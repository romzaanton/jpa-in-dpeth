CREATE TEXT SEARCH DICTIONARY russian_hunspell (
    TEMPLATE = ispell,
    DictFile = ru_ru,
    AffFile = ru_ru,
    StopWords = russian
);