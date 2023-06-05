package com.example.guice.cutominjection;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceCustomInjectionDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindListener(Matchers.any(), new LoggingListener());
            }
        });
        injector.getInstance(LoggingClient.class).doLogging("hello");
    }

    public static class LoggingClient {
        @Logging
        private Logger logger;

        public void doLogging(String content) {
            Optional.ofNullable(logger).ifPresent(l -> l.info(content));
        }
    }

    @Qualifier
    @Retention(RUNTIME)
    @interface Logging {}

    public static class LoggingMembersInjector<T> implements MembersInjector<T> {
        private final Field field;
        private final Logger logger;

        public LoggingMembersInjector(Field field) {
            this.field = field;
            this.logger = LoggerFactory.getLogger(field.getDeclaringClass());
            field.setAccessible(true);
        }

        @Override
        public void injectMembers(T instance) {
            try {
                field.set(instance, logger);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                field.setAccessible(false);
            }
        }
    }

    public static class LoggingListener implements TypeListener {

        @Override
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            Class<?> rawType = type.getRawType();
            while (Objects.nonNull(rawType)) {
                for (Field field : rawType.getDeclaredFields()) {
                    if (field.getType() == Logger.class && field.isAnnotationPresent(Logging.class)) {
                        encounter.register(new LoggingMembersInjector<>(field));
                    }
                }
                rawType = rawType.getSuperclass();
            }
        }
    }
}
