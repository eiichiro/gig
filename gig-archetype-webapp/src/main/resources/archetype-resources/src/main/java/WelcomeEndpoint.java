package ${package};

import static org.eiichiro.bootleg.MediaType.*;
import static org.eiichiro.bootleg.Verb.*;

import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.bootleg.annotation.Allows;
import org.eiichiro.bootleg.annotation.Endpoint;
import org.eiichiro.bootleg.annotation.Generates;

@Name("${package}.WelcomeEndpoint")
@Endpoint
public class WelcomeEndpoint {

	@Inject private WelcomeService service;
	
	@Allows(GET)
	@Generates(APPLICATION_JSON)
	public Welcome get() {
		return service.get();
	}
	
}
