package com.example.guice.multibinder;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import java.util.Optional;
import java.util.Set;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceMultiBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Multibinder<Processor> multibinder = Multibinder.newSetBinder(binder(), Processor.class);
                multibinder
                        .permitDuplicates()
                        .addBinding()
                        .to(FirstProcessor.class)
                        .in(Scopes.SINGLETON);
                multibinder
                        .permitDuplicates()
                        .addBinding()
                        .to(SecondProcessor.class)
                        .in(Scopes.SINGLETON);
            }
        });
        injector.getInstance(Client.class).process();
    }

    @Singleton
    public static class Client {

        @Inject
        private Set<Processor> processors;

        public void process() {
            Optional.ofNullable(processors).ifPresent(ps -> ps.forEach(Processor::process));
        }
    }

    interface Processor {

        void process();
    }

    public static class FirstProcessor implements Processor {

        @Override
        public void process() {
            System.out.println("FirstProcessor process...");
        }
    }

    public static class SecondProcessor implements Processor {

        @Override
        public void process() {
            System.out.println("SecondProcessor process...");
        }
    }
}
