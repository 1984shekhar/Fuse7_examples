package TestPackage;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ForceException implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		
		
		throw new Exception();
		
	}

}
