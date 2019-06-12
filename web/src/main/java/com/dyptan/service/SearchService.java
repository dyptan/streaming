package com.dyptan.service;

import com.dyptan.configuration.ElasticConfiguration;
import com.dyptan.connector.SearchConnector;
import com.dyptan.model.Filter;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    Logger log = Logger.getLogger(this.getClass().getName());

    RestHighLevelClient client;
    String index;

    @Autowired
    public SearchService(SearchConnector connector, ElasticConfiguration configuration) {
        client = connector.getClient();
        index = configuration.getIndex();
    }

    public SearchHit[] getAllHits(SearchRequest request) {
        SearchResponse response = null;
        try { response = client.search(request);
        } catch (IOException e)
        { e.printStackTrace();}
        log.debug("Hits: " + response.getHits().totalHits);
        return response.getHits().getHits();
    }

    public SearchHit[] getFilteredHits(Filter filter) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("model", filter.getModels()))
                        .must(QueryBuilders.matchQuery("category", filter.getBrands()))
                        .filter(QueryBuilders.rangeQuery("year")
                                .from(filter.getYearFrom())
                                .to(filter.getYearTo()))
                        .filter(QueryBuilders.rangeQuery("price_usd")
                                .from(filter.getPriceFrom())
                                .to(filter.getPriceTo()))
                        .filter(QueryBuilders.rangeQuery("published")
                                .gte("now-"+filter.getPeriodRange()+filter.getPeriodMultiplier().getAbbreviation())
                                .lt("now"))
                );
        log.debug("Search request built: " + searchSourceBuilder.toString());
        return getAllHits(request.source(searchSourceBuilder.size(1000).sort("published", SortOrder.DESC)));
    }

    public List<Map<String, Object>> getHitsAsList(Filter filter) {
        SearchHit[] hits = getFilteredHits(filter);
        List<Map<String, Object>> documents = Arrays.stream(hits)
                .map(SearchHit::getSourceAsMap)
                .collect(Collectors.toList());
        return documents;
    }

    public Set<String> getBrands() {
        SearchRequest request = new SearchRequest(index).source(new SearchSourceBuilder().size(1000)
                .query(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("published")
                                .gte("now-1w")
                                .lt("now"))
                ));

        SearchHit[] hits = getAllHits(request);
        Set<String> brands = Arrays.stream(hits)
                .map(x -> x.getSourceAsMap())
                .map(x -> (String) x.get("category"))
                .collect(Collectors.toSet());
        log.debug("Brands available" + brands);
        return brands;
    }
}
