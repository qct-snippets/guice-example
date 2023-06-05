package com.example.guice.justintimebinding;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;

/**
 * JIT Binding也就是Just-In-Time Binding，也可以称为隐式绑定（Implicit Binding）。隐式绑定需要满足：
 * 1.构造函数必须无参，并且非private修饰
 * 2.没有在Module实现中激活Binder#requireAtInjectRequired()
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceJustInTimeBindingDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
            }
        });
        Foo instance = injector.getInstance(Key.get(Foo.class));
    }

    public static class Foo {

        public Foo() {
            System.out.println("init Foo...");
        }
    }

    /**
     * 特化的Linked Binding，用于运行时绑定对应的目标类型.
     */
    @ImplementedBy(MessageProcessor.class)
    public interface Processor {}

    public static class MessageProcessor implements Processor {}

    /**
     * 特化的Provider Binding，用于运行时绑定对应的目标类型的Provider实现.
     */
    @ProvidedBy(DruidDataSource.class)
    public interface DataSource {}

    public static class DruidDataSource implements Provider<DataSource>, DataSource {

        private final DataSource dataSource = new DruidDataSource();

        @Override
        public DataSource get() {
            return dataSource;
        }
    }
}
