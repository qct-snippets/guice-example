package com.example.guice.providerbinding;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceProvidesBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {}

            @Provides
            @Singleton
            public Foo fooProvider() {
                return new Foo();
            }
        });
        Foo foo1 = injector.getInstance(Key.get(Foo.class));
        Foo foo2 = injector.getInstance(Key.get(Foo.class));
        System.out.println(foo1);
        System.out.println(foo2);
    }

    public static class Foo {}
}
