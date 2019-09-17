package com.derek.mydagger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

@SupportedAnnotationTypes("com.derek.mydagger.anno.*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ViewInjectorProcessor extends AbstractProcessor {

    // 所有注解处理器的列表
    List<AnnotationHandler> mHandlers = new LinkedList<>();
    //类型与字段的关联表，用于在写入Java文件时按类型来写不同的文件和字段
    final Map<String,List<VariableElement>> map = new HashMap<>();

    // 生成辅助类的Writer类
    AdapterWriter mWriter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        registerHandlers();
        mWriter = new DefaultJavaFileWriter(processingEnv);
    }

    private void registerHandlers(){
        mHandlers.add(new ViewInjectorHandler());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 迭代所有的注解处理器，使得每个注解都有一个处理器
        for (AnnotationHandler handler: mHandlers){
            handler.attachProcessingEnv(processingEnv);
            map.putAll(handler.handleAnnotation(roundEnvironment));
        }
        mWriter.generate(map);
        return true;
    }
}
