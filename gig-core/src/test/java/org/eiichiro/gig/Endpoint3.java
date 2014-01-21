package org.eiichiro.gig;

import org.eiichiro.jaguar.WebFilter;
import org.eiichiro.bootleg.annotation.Endpoint;

@Endpoint
public class Endpoint3 extends Endpoint2 implements Endpoint1 {
	
	public void test() {
		if (WebFilter.request() == null) {
			throw new RuntimeException();
		}
	}
	
}
