package io.code.art.jpa.in.depth.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryRewriter;

@Slf4j
public class ClearingRecordQueryRewriter implements QueryRewriter {
    @Override
    public String rewrite(String query, Sort sort) {
        log.info("CLEARING_RECORD_QUERY_REWRITER: query rewritten {}", query);
        return query;
    }
}
