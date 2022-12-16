package mx.arquidiocesis.eamxredsocialmodule.news.create.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object FileHelper {

    fun checkPermissions(context: Context?, onPermissionsGranted: () -> Unit) {
        val dialog = context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Aviso")
                .setMessage("Por favor conceda los permisos para acceder a su galería")
        }
        val listener = object : MultiplePermissionsListener {
            override fun onPermissionsChecked(permissions: MultiplePermissionsReport?) {
                if (permissions?.areAllPermissionsGranted() == true) {
                    onPermissionsGranted()
                } else {
                    dialog?.show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                dialog?.show()
            }
        }

        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(listener)
            .check()
    }

    fun pickMultipleMedia(fragment: Fragment, code: Int, tipo: Int) {
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).also {
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        when (tipo) {
            1 -> {
                pickIntent.type = "image/*"
            }
            2 -> {
                pickIntent.type = "video/*"
            }
            3 -> {
                pickIntent.type = "file/*"
            }
            else->{
                pickIntent.type = "image/* video/*"
            }
        }
        fragment.startActivityForResult(pickIntent, code)
    }

    fun pickSingleMedia(fragment: Fragment, code: Int) {
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = "image/* video/*"
        fragment.startActivityForResult(pickIntent, code)
    }

}