package com.baidu.fbu.mtp.util;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.baidu.fbu.mtp.common.util.Filter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.aop.framework.Advised;

public class ComponentUtils {

    public static <T> void sortFilters(List<T> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            return;
        }
        Collections.sort(filters, (o1, o2) -> {
            Filter annotation1 =  getAnnotation(o1, Filter.class);
            Filter annotation2 =  getAnnotation(o2, Filter.class);
            if (annotation1 == null && annotation2 != null) {
                return 1;
            }
            if (annotation2 == null) {
                return -1;
            }
            return annotation2.order() - annotation1.order();
        });
    }
    
    public static <T, A extends Annotation> A getAnnotation(T handler, Class<A> annotationClazz) {
        A annotation =  handler.getClass().getAnnotation(annotationClazz);
        if (annotation == null) {
            if (handler instanceof Advised) {
                Advised advised = (Advised) handler;
                try {
                    annotation = advised.getTargetSource().getTarget().getClass().getAnnotation(annotationClazz);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return annotation;
    }

}
