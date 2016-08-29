//package com.mapbar.android.obd.rearview.lib.mvp;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//
///**
// * Created by zhangyunfei on 16/8/29.
// */
//public class ViewProxy implements InvocationHandler {
//    @Override
//    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
//        System.out.println("proxy:" + method.getClass().getName());
//        System.out.println("method:" + method.getName());
//        System.out.println("args:" + args[0].getClass().getName());
//
//        System.out.println("Before invoke method...");
//        Object object = method.invoke(concreteClass, args);//普通的Java反射代码,通过反射执行某个类的某方法
//        //System.out.println(((ConcreteClass)concreteClass).targetMethod(10)+(Integer)args[0]);
//        System.out.println("After invoke method...");
//        return object;
//    }
//}
