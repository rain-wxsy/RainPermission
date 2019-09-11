package com.rain.library;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2019, by your 爱空间, All rights reserved.
 * -----------------------------------------------------------------
 *
 * File: RainKnife.java
 * Author: rxy
 * Version: V100R001C01
 * Create: 2019-09-11 10:04
 *
 * Changes (from 2019-09-11)
 * -----------------------------------------------------------------
 * 2019-09-11 : Create RainKnife.java (rxy);
 * -----------------------------------------------------------------
 */
public class RainKnife {

    public static void bind(Activity host) {
        Class clazz = host.getClass();
        String className = clazz.getCanonicalName() + "PermissionExpand";
        try {
            Class permissionClass = Class.forName(className);
            Method method = permissionClass.getMethod("bind", clazz);
            method.setAccessible(true);
            method.invoke(permissionClass.newInstance(), host);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
