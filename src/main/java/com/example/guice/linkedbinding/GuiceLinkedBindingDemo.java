package com.example.guice.linkedbinding;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceLinkedBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Foo.class).to(Bar.class).in(Scopes.SINGLETON);
            }
        });
        Foo foo = injector.getInstance(Foo.class);
        System.out.println(foo);
    }

    interface Foo {}

    public static class Bar implements Foo {}
}
