package net.chibidevteam.restappliseed.main.annotation;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import net.chibidevteam.utils.helper.ClassHelper;

public class RenamingProcessor extends ServletModelAttributeMethodProcessor
        implements EmbeddedValueResolverAware, ApplicationContextAware {

    private RequestMappingHandlerAdapter             requestMappingHandlerAdapter;

    // Rename cache
    private final Map<Class<?>, Map<String, String>> replaceMap = new ConcurrentHashMap<>();

    private StringValueResolver                      placeholderResolver;

    private ApplicationContext                       applicationContext;

    public RenamingProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest nativeWebRequest) {
        Object target = binder.getTarget();
        Class<?> targetClass = target.getClass();
        if (!replaceMap.containsKey(targetClass)) {
            Map<String, String> mapping = analyzeClass(targetClass);
            replaceMap.put(targetClass, mapping);
        }
        Map<String, String> mapping = replaceMap.get(targetClass);
        ParamNameDataBinder paramNameDataBinder = new ParamNameDataBinder(target, binder.getObjectName(), mapping);
        if (requestMappingHandlerAdapter == null) {
            requestMappingHandlerAdapter = applicationContext.getBean(RequestMappingHandlerAdapter.class);
        }
        requestMappingHandlerAdapter.getWebBindingInitializer().initBinder(paramNameDataBinder, nativeWebRequest);
        super.bindRequestParameters(paramNameDataBinder, nativeWebRequest);
    }

    private Map<String, String> analyzeClass(Class<?> targetClass) {
        List<Field> fields = ClassHelper.getAllFields(targetClass);
        Map<String, String> renameMap = new HashMap<>();
        for (Field field : fields) {
            ParamName paramNameAnnotation = field.getAnnotation(ParamName.class);
            if (paramNameAnnotation != null && !paramNameAnnotation.value().isEmpty()) {
                String value = placeholderResolver.resolveStringValue(paramNameAnnotation.value());
                renameMap.put(value, field.getName());
            }
        }
        if (renameMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return renameMap;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}