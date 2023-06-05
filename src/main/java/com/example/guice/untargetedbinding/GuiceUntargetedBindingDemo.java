package com.example.guice.untargetedbinding;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceUntargetedBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Foo.class).in(Scopes.SINGLETON);
                bind(Bar.class).annotatedWith(Names.named("bar")).to(Bar.class).in(Scopes.SINGLETON);
            }
        });
        Bar instance = injector.getInstance(Bar.class);
    }

    public static class Foo {}

    public static class Bar {}
}
