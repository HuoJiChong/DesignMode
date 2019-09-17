package com.derek.mydagger;

import com.derek.mydagger.anno.ViewInjector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public class ViewInjectorHandler implements AnnotationHandler {
    ProcessingEnvironment mProcessingEnv;

    @Override
    public void attachProcessingEnv(ProcessingEnvironment processingEnv) {
        mProcessingEnv = processingEnv;
    }

    @Override
    public Map<? extends String, ? extends List<VariableElement>> handleAnnotation(RoundEnvironment roundEnvironment) {
        Map<String,List<VariableElement>> annotationMap = new HashMap<>();
        // 1、获取使用ViewInjector注解的所有元素
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(ViewInjector.class);
        for (Element element : elementSet){
            // 2、获取被注解的字段
            VariableElement varElement = (VariableElement) element;
            // 3、获取字段所在类型的完整路径名，如一个TextView所在的Activity的完整路径，也就是变量的宿主类
            String className = getParentClassName(varElement);
            // 4、获取这个宿主类型的所有元素，如某个Activity中的所有注解对象
            List<VariableElement> cacheElements = annotationMap.get(className);
            if (cacheElements == null){
                cacheElements = new LinkedList<>();
            }
            // 将元素添加到该类型对应的字段列表中
            cacheElements.add(varElement);
            // 以宿主类的路径为key,所有字段列表为value，存入map
            // 这里是将所在字段按所属的类型进行分类
            annotationMap.put(className,cacheElements);

        }

        return annotationMap;
    }

    /**
     * 待处理
     * @param variableElement
     * @return
     */
    private String getParentClassName(VariableElement variableElement){
        return variableElement.getSimpleName().toString();
    }
}
