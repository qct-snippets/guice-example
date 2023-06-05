package com.example.guice.constructorbinding;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceConstructorBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                try {
                    bind(Key.get(JdbcTemplate.class))
                            .toConstructor(DefaultJdbcTemplate.class.getConstructor(DataSource.class))
                            .in(Scopes.SINGLETON);
                } catch (NoSuchMethodException e) {
                    addError(e);
                }
            }
        });
        JdbcTemplate instance = injector.getInstance(JdbcTemplate.class);
    }

    interface JdbcTemplate {}

    public static class DefaultJdbcTemplate implements JdbcTemplate {

        public DefaultJdbcTemplate(DataSource dataSource) {
            System.out.println("init JdbcTemplate,ds => " + dataSource.hashCode());
        }
    }

    public static class DataSource {}
}
