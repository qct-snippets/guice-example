package com.example.guice.basic;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceBasicDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new DemoModule());
        Greeter first = injector.getInstance(Greeter.class);
        Greeter second = injector.getInstance(Greeter.class);
        System.out.printf("first hashcode => %s\n", first.hashCode());
        first.sayHello();
        System.out.printf("second hashcode => %s\n", second.hashCode());
        second.sayHello();
    }

    @Singleton
    public static class Greeter {

        private final String message;

        private final Integer count;

        @Inject
        public Greeter(@Message String message, @Count Integer count) {
            this.message = message;
            this.count = count;
        }

        public void sayHello() {
            for (int i = 1; i <= count; i++) {
                System.out.printf("%s,count => %d\n", message, i);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Count {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Message {}

    public static class DemoModule extends AbstractModule {

        @Override
        public void configure() {
            //            bind(Greeter.class).in(Scopes.SINGLETON);
        }

        @Provides
        @Count
        public static Integer count() {
            return 2;
        }

        @Provides
        @Count
        public static String message() {
            return "vlts.cn";
        }
    }
}
