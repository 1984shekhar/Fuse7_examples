package org.mycompany;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class RequestProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		SearchRequest searchRequest = new SearchRequest(); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		searchSourceBuilder.from(0); 
		searchSourceBuilder.size(15); 
		searchRequest.source(searchSourceBuilder);
		exchange.getIn().setBody(searchRequest);
	}

}
