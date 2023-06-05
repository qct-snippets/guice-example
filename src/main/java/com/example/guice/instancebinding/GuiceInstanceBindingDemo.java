package com.example.guice.instancebinding;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceInstanceBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Bar bar = new Bar();
                bind(Foo.class).toInstance(bar);
            }
        });
        Foo foo = injector.getInstance(Foo.class);
    }

    interface Foo {}

    public static class Bar implements Foo {}
}
