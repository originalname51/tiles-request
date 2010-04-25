package org.apache.tiles.request.scope;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.util.RequestWrapper;

public class ReflectionContextResolver implements ContextResolver {

    private Map<Class<? extends Request>, Set<String>> class2scopes = new HashMap<Class<? extends Request>, Set<String>>();

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getContext(Request request, String scope) {
        String methodName = "get" + Character.toUpperCase(scope.charAt(0))
                + scope.substring(1) + "Scope";
        Method method;
        try {
            method = request.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            if (request instanceof RequestWrapper) {
                RequestWrapper wrapper = (RequestWrapper) request;
                return getContext(wrapper.getWrappedRequest(), scope);
            }
            throw new NoSuchScopeException("No accessor method for '" + scope
                    + "' scope.", e);
        }
        try {
            return (Map<String, Object>) method.invoke(request);
        } catch (IllegalAccessException e) {
            // Should not ever happen, since method is public.
            throw new NoSuchScopeException("No accessible method for '" + scope
                    + "' scope.", e);
        } catch (InvocationTargetException e) {
            throw new NoSuchScopeException(
                    "Exception during execution of accessor method for '"
                            + scope + "' scope.", e);
        }
    }

    @Override
    public String[] getAvailableScopes(Request request) {
        Set<String> scopes = new LinkedHashSet<String>();
        boolean finished = false;
        do {
            scopes.addAll(getSpecificScopeSet(request));
            if (request instanceof RequestWrapper) {
                request = ((RequestWrapper) request)
                        .getWrappedRequest();
            } else {
                finished = true;
            }
        } while(!finished);
        String[] retValue = new String[scopes.size()];
        return scopes.toArray(retValue);
    }

    private Set<String> getSpecificScopeSet(Request request) {
        Set<String> scopes = class2scopes.get(request.getClass());
        if (scopes == null) {
            scopes = new LinkedHashSet<String>();
            String[] nativeScopes = request.getNativeScopes();
            if (nativeScopes != null) {
                for (String scopeName: nativeScopes) {
                    scopes.add(scopeName);
                }
            }
            class2scopes.put(request.getClass(), scopes);
        }
        return scopes;
    }
}