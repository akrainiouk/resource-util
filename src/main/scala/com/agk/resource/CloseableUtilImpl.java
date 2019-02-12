package com.agk.resource;

import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Function3;
import scala.Function4;

class CloseableUtilImpl {

    static <A extends AutoCloseable, R>
    R tryWithResources(Function0<A> a, Function1<A, R> code) throws Exception {
	try (A x = a.apply()) {
	    return code.apply(x);
	}
    }

    static <A extends AutoCloseable, B extends AutoCloseable, R>
    R tryWithResources(Function0<A> a, Function0<B> b, Function2<A, B, R> code) throws Exception {
	try (
		A x = a.apply();
		B y = b.apply()
	) {
	    return code.apply(x, y);
	}
    }

    static <A extends AutoCloseable, B extends AutoCloseable, C extends AutoCloseable, R>
    R tryWithResources(Function0<A> a, Function0<B> b, Function0<C> c, Function3<A, B, C, R> code) throws Exception {
	try (
		A x = a.apply();
		B y = b.apply();
		C z = c.apply();
	) {
	    return code.apply(x, y, z);
	}
    }

    static <A extends AutoCloseable, B extends AutoCloseable, C extends AutoCloseable, D extends AutoCloseable, R>
    R tryWithResources(Function0<A> a, Function0<B> b, Function0<C> c, Function0<D> d, Function4<A, B, C, D, R> code) throws Exception {
	try (
		A r1 = a.apply();
		B r2 = b.apply();
		C r3 = c.apply();
		D r4 = d.apply()
	) {
	    return code.apply(r1, r2, r3, r4);
	}
    }

}