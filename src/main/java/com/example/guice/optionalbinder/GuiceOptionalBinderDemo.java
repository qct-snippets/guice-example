package com.example.guice.optionalbinder;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.multibindings.OptionalBinder;
import java.util.Optional;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceOptionalBinderDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                OptionalBinder<Logger> optionalBinder = OptionalBinder.newOptionalBinder(binder(), Logger.class);
                optionalBinder.setDefault().to(StdLogger.class).in(Scopes.SINGLETON);
            }
        });
        injector.getInstance(Client.class).log("Hello");
    }

    public static class Client {

        @Inject
        private Optional<Logger> logger;

        public void log(String content) {
            logger.ifPresent(l -> l.log(content));
        }
    }

    interface Logger {

        void log(String content);
    }

    public static class StdLogger implements Logger {

        @Override
        public void log(String content) {
            System.out.println(content);
        }
    }
}
