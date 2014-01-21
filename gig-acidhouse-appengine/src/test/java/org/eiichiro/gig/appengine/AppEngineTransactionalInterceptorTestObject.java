package org.eiichiro.gig.appengine;

import org.eiichiro.acidhouse.appengine.AppEngineDatastoreSession;
import org.eiichiro.gig.Transactional;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.inject.Name;

@Name("AppEngineTransactionalInterceptorTestObject")
public class AppEngineTransactionalInterceptorTestObject {

	@Inject private AppEngineDatastoreSession session;
	
	@Transactional
	public void method1() {
		AppEngineTransactionalInterceptorTestEntity entity = new AppEngineTransactionalInterceptorTestEntity();
		entity.key = "Key1";
		session.put(entity);
	}
	
	@Transactional
	public void method2() {
		AppEngineTransactionalInterceptorTestEntity entity = new AppEngineTransactionalInterceptorTestEntity();
		entity.key = "Key2";
		session.put(entity);
		throw new RuntimeException();
	}
	
}
