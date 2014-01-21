package org.eiichiro.gig;

import org.eiichiro.jaguar.Descriptor;
import org.eiichiro.jaguar.scope.Context;

public class Scope1Context implements Context {

	@Override
	public <T> T get(Descriptor<T> descriptor) {
		return null;
	}

	@Override
	public <T> void put(Descriptor<T> descriptor, T t) {}

	@Override
	public Object store() {
		// TODO Auto-generated method stub
		return null;
	}

}
