package org.eiichiro.gig.appengine;

import org.eiichiro.acidhouse.appengine.AppEngineStrongDatastoreSession;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.inject.Name;

@Name("AppEngineGlobalTransactionalInterceptorTestObject")
public class AppEngineGlobalTransactionalInterceptorTestObject {

	@Inject private AppEngineStrongDatastoreSession session;
	
	@GlobalTransactional
	public void method1() {
		AppEngineGlobalTransactionalInterceptorTestEntity entity = new AppEngineGlobalTransactionalInterceptorTestEntity();
		entity.key = "Key1";
		session.put(entity);
	}
	
	@GlobalTransactional
	public void method2() {
		AppEngineGlobalTransactionalInterceptorTestEntity entity = new AppEngineGlobalTransactionalInterceptorTestEntity();
		entity.key = "Key2";
		session.put(entity);
		throw new RuntimeException();
	}
	
}
