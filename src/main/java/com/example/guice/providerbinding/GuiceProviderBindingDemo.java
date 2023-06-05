package com.example.guice.providerbinding;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceProviderBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Key.get(Foo.class)).toProvider(FooProvider.class).in(Scopes.SINGLETON);
            }
        });
        Foo foo1 = injector.getInstance(Key.get(Foo.class));
        Foo foo2 = injector.getInstance(Key.get(Foo.class));
        System.out.println(foo1);
        System.out.println(foo2);
    }

    public static class Foo {}

    public static class FooProvider implements Provider<Foo> {
        private final Foo foo = new Foo();

        @Override
        public Foo get() {
            System.out.println("Get Foo from FooProvider...");
            return foo;
        }
    }
}
