package mephi.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DIContainer {
    // Готовые созданные объекты (Singleton-инстансы)
    private final Map<Class<?>, Object> instances = new HashMap<>();

    // Зарегистрированные классы-провайдеры, которые мы умеем создавать
    private final Map<Class<?>, Class<?>> providers = new HashMap<>();

    // Метод для ручной регистрации готового объекта
    public <T> void registerInstance(Class<T> type, T instance) {
        instances.put(type, instance);
    }

    // Метод для регистрации класса-реализации для определенного типа (интерфейса или класса)
    public <T> void registerProvider(Class<T> type, Class<? extends T> implementationClass) {
        providers.put(type, implementationClass);
    }

    // принимает массив объектов (классов или уже готовых инстансов)
    public void provide(Object... providersList) {
        for (Object provider : providersList) {
            if (provider instanceof Class<?> clazz) {
                // Вызываем вспомогательный метод, который безопасно свяжет типы
                registerProviderSelf(clazz);
            } else {
                // Приводим к Class<?> для совместимости, так как точный тип рантайм-объекта нам не важен
                instances.put((Class<?>) provider.getClass(), provider);
            }
        }
    }

    // Получение зависимости с ленивой инициализацией
    public <T> T get(Class<T> type) {
        // 1. Если объект уже создан, просто возвращаем его
        if (instances.containsKey(type)) {
            return (T) instances.get(type);
        }

        // 2. Если объект еще не создан, но у нас есть рецепт (класс) для его создания
        if (providers.containsKey(type)) {
            Class<?> implementationClass = providers.get(type);
            return (T) createInstance(implementationClass);
        }

        throw new RuntimeException("Провайдер не найден для класса: " + type.getName() +
                ". Забыли передать его в provide()?");
    }

    private <T> void registerProviderSelf(Class<T> clazz) {
        registerProvider(clazz, clazz);
    }

    // Рекурсивное создание инстанса
    private <T> T createInstance(Class<T> implementationClass) {
        try {
            if (implementationClass.getConstructors().length == 0) {
                throw new RuntimeException("Нет публичных конструкторов у класса " + implementationClass.getName());
            }

            Constructor<?> constructor = implementationClass.getConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            List<Object> arguments = new ArrayList<>();

            // Рекурсивно запрашиваем каждую зависимость через get()
            for (Class<?> paramType : parameterTypes) {
                arguments.add(get(paramType));
            }

            // Создаем объект
            T instance = (T) constructor.newInstance(arguments.toArray());

            // Сохраняем в карту инстансов, чтобы не создавать повторно
            instances.put(implementationClass, instance);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось автоматически создать объект для " + implementationClass.getName(), e);
        }
    }
}
