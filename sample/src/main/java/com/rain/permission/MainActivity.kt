package com.rain.permission

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rain.lib.annotation.PermissionDenied
import com.rain.lib.annotation.PermissionNeverAsk
import com.rain.lib.annotation.PermissionRequest
import com.rain.lib.annotation.RuntimePermissions
import com.rain.library.RainKnife

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RainKnife.bind(this)
        MainActivityPermissionExpand.doSomethingWithCheckPermission(
            this, arrayOf(Manifest.permission.CAMERA),
            PERMISSION_CAMERA
        ) {
            showCamera()
        }
    }

    @PermissionRequest(requestCode = PERMISSION_CAMERA)
    fun showCamera() {

    }

    @PermissionDenied(deniedCode = PERMISSION_CAMERA)
    fun deniedCamera() {

    }

    @PermissionNeverAsk(neverAskCode = PERMISSION_CAMERA)
    fun neverAskCamera() {

    }

    companion object {
        private const val PERMISSION_CAMERA = 101
        private const val PERMISSION_RADIO = 102
        private const val PERMISSION_PHONE = 103

    }
}
