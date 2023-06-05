package com.example.guice.autoscan;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 *
 * Guice本身不提供类路径或者Jar文件的类扫描功能，要实现类路径下的所有Bean全自动注册绑定，需要依赖第三方类扫描框架，
 * 这里选用了一个性能比较高社区比较活跃的类库io.github.classgraph:classgraph.
 * <p>Created by qct on 2023/6/5.
 */
public class GuiceAutoScanDemo {
    public static void main(String[] args) {
        GuiceAutoScanModule module = new GuiceAutoScanModule(new String[] {"cn.vlts"}, new String[] {"*Demo", "*Test"});
        Injector injector = Guice.createInjector(module);
        Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();
        System.out.println(allBindings);
    }

    @RequiredArgsConstructor
    public static class GuiceAutoScanModule extends AbstractModule {
        private final Set<Class<?>> bindClasses = new HashSet<>();

        private final String[] acceptPackages;

        private final String[] rejectClasses;

        @Override
        protected void configure() {
            ClassGraph classGraph = new ClassGraph();
            ScanResult scanResult = classGraph
                    .enableClassInfo()
                    .acceptPackages(acceptPackages)
                    .rejectClasses(rejectClasses)
                    .scan();
            ClassInfoList allInterfaces = scanResult.getAllInterfaces();
            for (ClassInfo i : allInterfaces) {
                ClassInfoList impl = scanResult.getClassesImplementing(i.getName());
                if (Objects.nonNull(impl)) {
                    Class<?> ic = i.loadClass();
                    int size = impl.size();
                    if (size > 1) {
                        for (ClassInfo im : impl) {
                            Class<?> implClass = im.loadClass();
                            if (isSingleton(implClass)) {
                                String simpleName = im.getSimpleName();
                                String name = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
                                bindNamedSingleInterface(ic, name, implClass);
                            }
                        }
                    } else {
                        for (ClassInfo im : impl) {
                            Class<?> implClass = im.loadClass();
                            if (isProvider(implClass)) {
                                bindProvider(ic, implClass);
                            }
                            if (isSingleton(implClass)) {
                                bindSingleInterface(ic, implClass);
                            }
                        }
                    }
                }
            }

            ClassInfoList standardClasses = scanResult.getAllStandardClasses();
            for (ClassInfo ci : standardClasses) {
                Class<?> implClass = ci.loadClass();
                if (!bindClasses.contains(implClass) && shouldBindSingleton(implClass)) {
                    bindSingleton(implClass);
                }
            }
            bindClasses.clear();
            ScanResult.closeAll();
        }

        private boolean shouldBindSingleton(Class<?> implClass) {
            int modifiers = implClass.getModifiers();
            return isSingleton(implClass) && !Modifier.isAbstract(modifiers) && !implClass.isEnum();
        }

        private void bindSingleton(Class<?> implClass) {
            bindClasses.add(implClass);
            bind(implClass).in(Scopes.SINGLETON);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void bindSingleInterface(Class<?> ic, Class<?> implClass) {
            bindClasses.add(implClass);
            bind((Class) ic).to(implClass).in(Scopes.SINGLETON);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void bindNamedSingleInterface(Class<?> ic, String name, Class<?> implClass) {
            bindClasses.add(implClass);
            bind((Class) ic).annotatedWith(Names.named(name)).to(implClass).in(Scopes.SINGLETON);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private <T> void bindProvider(Class<?> ic, Class<?> provider) {
            bindClasses.add(provider);
            Type type = ic.getGenericInterfaces()[0];
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class target = (Class) parameterizedType.getActualTypeArguments()[0];
            bind(target).toProvider(provider).in(Scopes.SINGLETON);
        }

        private boolean isSingleton(Class<?> implClass) {
            return Objects.nonNull(implClass) && implClass.isAnnotationPresent(Singleton.class);
        }

        private boolean isProvider(Class<?> implClass) {
            return isSingleton(implClass) && Provider.class.isAssignableFrom(implClass);
        }
    }
}
