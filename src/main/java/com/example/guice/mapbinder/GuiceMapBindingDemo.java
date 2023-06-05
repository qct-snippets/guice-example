package com.example.guice.mapbinder;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceMapBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                MapBinder<Type, Processor> mapBinder = MapBinder.newMapBinder(binder(), Type.class, Processor.class);
                mapBinder.addBinding(Type.SMS).to(SmsProcessor.class).in(Scopes.SINGLETON);
                mapBinder
                        .addBinding(Type.MESSAGE_TEMPLATE)
                        .to(MessageTemplateProcessor.class)
                        .in(Scopes.SINGLETON);
            }
        });
        injector.getInstance(Client.class).process();
    }

    @Singleton
    public static class Client {
        @Inject
        private Map<Type, Processor> processors;

        public void process() {
            Optional.ofNullable(processors).ifPresent(ps -> ps.forEach((type, processor) -> processor.process()));
        }
    }

    interface Processor {

        void process();
    }

    public static class SmsProcessor implements Processor {

        @Override
        public void process() {
            System.out.println("SmsProcessor process...");
        }
    }

    public static class MessageTemplateProcessor implements Processor {

        @Override
        public void process() {
            System.out.println("MessageTemplateProcessor process...");
        }
    }

    public enum Type {

        /**
         * 短信
         */
        SMS,

        /**
         * 消息模板
         */
        MESSAGE_TEMPLATE
    }
}
