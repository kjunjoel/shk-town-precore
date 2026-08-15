package kr.shkworld.shktown.core;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void registerService(Class<T> service, T serviceInstance) {
        services.put(service, serviceInstance);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }
}
