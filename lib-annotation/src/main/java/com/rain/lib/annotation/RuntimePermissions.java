package com.rain.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2019, by your 爱空间, All rights reserved.
 * -----------------------------------------------------------------
 *
 * File: RuntimePermissions.java
 * Author: rxy
 * Version: V100R001C01
 * Create: 2019-09-04 16:34
 *
 * Changes (from 2019-09-04)
 * -----------------------------------------------------------------
 * 2019-09-04 : Create RuntimePermissions.java (rxy);
 * -----------------------------------------------------------------
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RuntimePermissions {

}
