package com.rain.lib.process;

import com.google.auto.service.AutoService;
import com.rain.lib.annotation.RuntimePermissions;
import com.rain.lib.annotation.callback.PermissionNextListener;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2019, by your 爱空间, All rights reserved.
 * -----------------------------------------------------------------
 *
 * File: PermissionProcessor.java
 * Author: rxy
 * Version: V100R001C01
 * Create: 2019-09-04 16:37
 *
 * Changes (from 2019-09-04)
 * -----------------------------------------------------------------
 * 2019-09-04 : Create PermissionProcessor.java (rxy);
 * -----------------------------------------------------------------
 */
@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor {

    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(RuntimePermissions.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(RuntimePermissions.class);

        Map<String, Map<String, TypeElement>> typeMap = new HashMap<>();

        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            String fullName = typeElement.getQualifiedName().toString();
            Map<String, TypeElement> elementMap = typeMap.get(fullName);
            if (elementMap == null) {
                elementMap = new HashMap<>();
                elementMap.put(fullName, typeElement);
                typeMap.put(fullName, elementMap);
            }
        }

        for (String key : typeMap.keySet()) {
            Map<String, TypeElement> classElementMap = typeMap.get(key);
            TypeElement typeElement = classElementMap.get(key);
            String packageName = mElementUtils.getPackageOf(classElementMap.get(key)).toString();
            String className = typeElement.getSimpleName().toString() + "PermissionExpand";
            JavaFile javaFile = JavaFile.builder(packageName, generateJavaCode(className, typeElement))
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return true;
    }

    private TypeSpec generateJavaCode(String className, TypeElement typeElement) {

        FieldSpec reqFieldSpec = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, Integer.class, String.class),
                "reqMethods", Modifier.PRIVATE, Modifier.STATIC)
                .build();
        FieldSpec deniedFieldSpec = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, Integer.class, String.class),
                "deniedMethods", Modifier.PRIVATE, Modifier.STATIC)
                .build();
        FieldSpec neverAskFieldSpec = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, Integer.class, String.class),
                "neverAskMethods", Modifier.PRIVATE, Modifier.STATIC)
                .build();

        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("reqMethods = new HashMap<>()")
                .addStatement("deniedMethods = new HashMap<>()")
                .addStatement("neverAskMethods = new HashMap<>()");
        CodeBlock codeBlock = builder.build();

        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addField(reqFieldSpec)
                .addField(deniedFieldSpec)
                .addField(neverAskFieldSpec)
                .addStaticBlock(codeBlock)
                .addMethod(generateMethodCode(typeElement))
                .addMethod(generateBindMethod(typeElement))
                .addMethod(generatePermissionResultMethod(typeElement))
                .build();
    }

    private MethodSpec generateMethodCode(TypeElement typeElement) {
        ClassName host = ClassName.bestGuess(typeElement.getQualifiedName().toString());
        ClassName permissionClassName = ClassName.get("com.rain.library", "PermissionUtils");
        return MethodSpec.methodBuilder("doSomethingWithCheckPermission")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(host, "host")
                .addParameter(ParameterizedTypeName.get(String[].class), "permissions")
                .addParameter(int.class, "reqCode")
                .addParameter(PermissionNextListener.class, "listener")
                .beginControlFlow("if ($T.hasPermissions(host,permissions))", permissionClassName)
                .addStatement("listener.onNext()")
                .nextControlFlow("else")
                .addStatement("host.requestPermissions(permissions,reqCode)")
                .endControlFlow()
                .build();
    }

    private MethodSpec generateBindMethod(TypeElement typeElement) {
        ClassName host = ClassName.bestGuess(typeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(host, "host");

        ClassName methodClassName = ClassName.get("java.lang.reflect", "Method");
        ClassName annotationClassName = ClassName.get("java.lang.annotation", "Annotation");
        ClassName permissionDeniedClassName = ClassName.get("com.rain.lib.annotation", "PermissionDenied");
        ClassName permissionRequestClassName = ClassName.get("com.rain.lib.annotation", "PermissionRequest");
        ClassName permissionNeverAskClassName = ClassName.get("com.rain.lib.annotation", "PermissionNeverAsk");
        methodBuilder.addStatement("reqMethods.clear()")
                .addStatement("deniedMethods.clear()")
                .addStatement("neverAskMethods.clear()")
                .addStatement("Class clazz = host.getClass()")
                .addStatement("$T[] methods = clazz.getMethods()", methodClassName)
                .beginControlFlow("for (Method method : methods)")
                .addStatement("$T[] annotations = method.getDeclaredAnnotations()", annotationClassName)
                .beginControlFlow("for (Annotation annotation : annotations)")
                .beginControlFlow("if (annotation.annotationType() == $T.class)", permissionDeniedClassName)
                .addStatement("deniedMethods.put(((PermissionDenied) annotation).deniedCode(),method.getName())")
                .nextControlFlow("else if(annotation.annotationType() == $T.class)", permissionRequestClassName)
                .addStatement("reqMethods.put(((PermissionRequest) annotation).requestCode(),method.getName())")
                .nextControlFlow("else if(annotation.annotationType() == $T.class)", permissionNeverAskClassName)
                .addStatement("neverAskMethods.put(((PermissionNeverAsk) annotation).neverAskCode(),method.getName())")
                .endControlFlow()
                .endControlFlow()
                .endControlFlow();
        return methodBuilder.build();
    }

    private MethodSpec generatePermissionResultMethod(TypeElement typeElement) {
        ClassName host = ClassName.bestGuess(typeElement.getQualifiedName().toString());
        ClassName logClassName = ClassName.get("android.content.pm", "PackageManager");


        return MethodSpec.methodBuilder("requestPermissionResult")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(host, "host")
                .addParameter(String[].class, "permissions")
                .addParameter(int.class, "requestCode")
                .addParameter(ParameterizedTypeName.get(List.class, Integer.class), "grantResults")
                .addStatement("boolean granted=true")
                .beginControlFlow("for(int it:grantResults)")
                .beginControlFlow("if(it == $T.PERMISSION_DENIED)", logClassName)
                .addStatement("granted=false;\nbreak")
                .endControlFlow()
                .endControlFlow()
                .beginControlFlow("try")
                .addStatement("Class clazz = host.getClass()")
                .beginControlFlow("if(granted)")
                .addStatement("String methodName = reqMethods.get(requestCode)")
                .addStatement("Method curMethod = clazz.getMethod(methodName, Object[].class)")
                .addStatement("curMethod.invoke(host, Object[].class)")
                .nextControlFlow("else")
                .addStatement("String[] deniedPermissions = PermissionUtils.deniedPermissions(host, permissions)")
                .beginControlFlow("if(PermissionUtils.shouldShowPermissionDialog(host, deniedPermissions))")
                .addStatement("String methodName = deniedMethods.get(requestCode)")
                .addStatement("Method curMethod = clazz.getMethod(methodName, Object[].class)")
                .addStatement("curMethod.invoke(host, Object[].class)")
                .nextControlFlow("else")
                .addStatement("String methodName = neverAskMethods.get(requestCode)")
                .addStatement("Method curMethod = clazz.getMethod(methodName, Object[].class)")
                .addStatement("curMethod.invoke(host, Object[].class)")
                .endControlFlow()
                .endControlFlow()
                .nextControlFlow("catch ($T e)", NoSuchMethodException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", IllegalAccessException.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("catch ($T e)", InvocationTargetException.class)
                .addStatement("System.out.println(\"此处接收被调用方法内部未被捕获的异常\")")
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .build();
    }
}
