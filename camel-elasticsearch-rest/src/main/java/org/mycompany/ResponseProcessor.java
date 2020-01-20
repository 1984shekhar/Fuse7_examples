package org.mycompany;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class ResponseProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		SearchHits searchHits = exchange.getIn().getBody(SearchHits.class); 

		System.out.println("Size: "+searchHits.getHits().length);
		for(SearchHit searchHit:searchHits) {
			System.out.println("\nIndex: "+searchHit.getIndex());
			System.out.println("Type: "+searchHit.getType());
			System.out.println("Id: "+searchHit.getId());
			System.out.println("Source: "+searchHit.getSourceAsString());
		}
	}

}
