package org.eiichiro.gig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.eiichiro.jaguar.scope.Scope;

@Retention(RetentionPolicy.RUNTIME)
@Scope(Scope1Context.class)
public @interface Scope1 {}
