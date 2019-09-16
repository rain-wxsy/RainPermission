# RainPermission
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

android权限申请工具，简单、易用，几行代码即可搞定繁琐复杂的权限申请。

# 简单使用

    1.在activity的onCreate()方法中执行绑定操作
    RainKnife.bind(this)
    
    2.给有权限申请的activity或者fragment页面添加 RuntimePermissions注解
    @RuntimePermissions
    class MainActivity : AppCompatActivity()
    
    3.分别定义权限申请通过、申请被拒绝、申请被拒绝并选择了不再提示的方法，分别上架PermissionRequest、PermissionDenied、PermissionNeverAsk注解，参数为申请权限时的requestCode，在方法中执行相关逻辑
    
    @PermissionRequest(requestCode = PERMISSION_CAMERA)
    fun showCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, PERMISSION_CAMERA)
    }
    
    @PermissionDenied(deniedCode = PERMISSION_CAMERA)
    fun deniedCamera() {
        Toast.makeText(this, "用户拒绝了相机权限", Toast.LENGTH_SHORT).show()
        showDeniedDialog()
    }

    @PermissionNeverAsk(neverAskCode = PERMISSION_CAMERA)
    fun neverAskCamera() {
        toSetting()
    }
    
    4.托管点击事件，每个添加了RuntimePermissions注解的方法或类都会生成一个（类名+PermissionExpand）的类，直接调用doSomethingWithCheckPermission方法即可，需要传入上下文、权限组、requestCode和回调即可
    camera.setOnClickListener {
            MainActivityPermissionExpand.doSomethingWithCheckPermission(
                this, arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            ) {
                showCamera()
            }
        }
    
    5.托管onRequestPermissionResult方法传入当前的activity和onRequestPermissionsResult中的requestCode、permissions、grantResults即可
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainActivityPermissionExpand.requestPermissionResult(this, permissions, requestCode, grantResults.toList()
        )
    }

# 引用
kotlin项目

  implementation 'com.rain.permission:rainknife:1.0.3'
  
  kapt 'com.rain.permission:permission-compile:1.0.3'
  
java项目

   implementation 'com.rain.permission:rainknife:1.0.3'
   
   annotationProcessor 'com.rain.permission:permission-compile:1.0.3'
  
   
# LICENSE
    Copyright (c) 2015 LingoChamp Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
