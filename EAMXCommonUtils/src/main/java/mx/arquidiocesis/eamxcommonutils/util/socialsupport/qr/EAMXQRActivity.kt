package mx.arquidiocesis.eamxcommonutils.util.socialsupport.qr

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.util.permission.MULTIPLE_PERMISSIONS
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission

class EAMXQRActivity : AppCompatActivity() {
    private var instanceCamera: CodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eamx_qr_activity)

        if(UtilValidPermission().validListPermissionsAndBuildRequestActivity(
                this,
                arrayListOf(android.Manifest.permission.CAMERA)
                )) {
            initView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MULTIPLE_PERMISSIONS ->
                if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
                    initView()
                } else {
                    finish()
                }
        }
    }

    private fun initView(){
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        instanceCamera = CodeScanner(this, scannerView)

        this.instanceCamera?.apply {
            camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            CodeScanner.ALL_FORMATS  // list of type BarcodeFormat,
            autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = false // Whether to enable flash or not
        }

        this.instanceCamera?.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Error al escanear: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        this.instanceCamera?.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Ocurrio un error al intentar iniciar la cámara: ${it.message}",
                        Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            this.instanceCamera?.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        instanceCamera?.also { it.startPreview()}
    }

    override fun onPause() {
        instanceCamera?.also { it.releaseResources()}
        super.onPause()
    }
}