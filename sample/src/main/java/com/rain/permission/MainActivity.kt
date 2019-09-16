package com.rain.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.rain.lib.annotation.PermissionDenied
import com.rain.lib.annotation.PermissionNeverAsk
import com.rain.lib.annotation.PermissionRequest
import com.rain.lib.annotation.RuntimePermissions
import com.rain.library.RainKnife
import kotlinx.android.synthetic.main.activity_main.*

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RainKnife.bind(this)

        camera.setOnClickListener {
            MainActivityPermissionExpand.doSomethingWithCheckPermission(
                this, arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            ) {
                showCamera()
            }
        }
    }

    @SuppressLint("InlinedApi")
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

    @PermissionRequest(requestCode = PERMISSION_RADIO)
    fun openRecord() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        startActivity(intent)
    }

    @PermissionDenied(deniedCode = PERMISSION_RADIO)
    fun deniedRecord() {
        showDeniedDialog()
        Toast.makeText(this, "用户拒绝了录音权限", Toast.LENGTH_SHORT).show()
    }

    @PermissionNeverAsk(neverAskCode = PERMISSION_RADIO)
    fun neverAskRecord() {
        toSetting()
    }


    private fun showDeniedDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_message, null)
        val closeButton = view.findViewById<AppCompatButton>(R.id.closeButton)
        val permissionButton = view.findViewById<AppCompatButton>(R.id.permissionButton)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        permissionButton.setOnClickListener {
            dialog.dismiss()
            MainActivityPermissionExpand.doSomethingWithCheckPermission(
                this, arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            ) {
                showCamera()
            }
        }
        dialog.show()
    }

    private fun toSetting() {
        val dialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_message, null)
        val closeButton = view.findViewById<AppCompatButton>(R.id.closeButton)
        val permissionButton = view.findViewById<AppCompatButton>(R.id.permissionButton)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        permissionButton.setOnClickListener {
            dialog.dismiss()
            openSetting()
        }
        dialog.show()
    }

    private fun openSetting() {
//        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        val uri = Uri.fromParts("package", packageName, null)
//        intent.data = uri
//        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainActivityPermissionExpand.requestPermissionResult(
            this,
            permissions,
            requestCode,
            grantResults.toList()
        )
    }

    companion object {
        private const val PERMISSION_CAMERA = 101
        private const val PERMISSION_RADIO = 102
        private const val PERMISSION_PHONE = 103

    }
}
