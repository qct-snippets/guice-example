package com.example.guice.aop;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.matcher.Matchers;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceAopDemo {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindInterceptor(Matchers.only(EchoService.class), Matchers.any(), new EchoMethodInterceptor());
            }
        });

        EchoService echoService = injector.getInstance(Key.get(EchoService.class));
        echoService.echo("Alice");
    }

    public static class EchoService {

        public void echo(String name) {
            System.out.println(name + " echo");
        }
    }

    public static class EchoMethodInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            Method method = methodInvocation.getMethod();
            String methodName = method.getName();
            long start = System.nanoTime();
            System.out.printf("Before invoke method => [%s]\n", methodName);
            Object result = methodInvocation.proceed();
            long end = System.nanoTime();
            System.out.printf("After invoke method => [%s], cost => %d ns\n", methodName, (end - start));
            return result;
        }
    }
}
