package mx.arquidiocesis.eamxgeneric.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Tasks
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.installErrorCode
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnums
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.ProgressDialog
import mx.arquidiocesis.eamxcommonutils.util.EAMXCUMySharedPreferences
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxgeneric.BuildConfig
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.config.RemoteConfigFirebase
import mx.arquidiocesis.eamxgeneric.databinding.EamxSplashActivityBinding
import mx.arquidiocesis.eamxloginmodule.ui.EAMXLoginActivity


open class EAMXSplashActivity : AppCompatActivity(), InstallStateUpdatedListener {

    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }
    lateinit var mBinding: EamxSplashActivityBinding
    private val codeResultUpdateForce = 401
    private val codeResultUpdate = 400

    companion object {
        var signOutFromPerfil = false
    }

    private val progresDialog by lazy {
        ProgressDialog()
            .setTitle("Descargando actualizacion")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eamx_splash_activity)
        findViewById<TextView>(R.id.txtSplash).text = String.format(
            getString(R.string.texto_splash),
            "v${BuildConfig.VERSION_NAME + if (BuildConfig.FLAVOR != "pro") " - ${BuildConfig.FLAVOR}" else ""}"
        )
        mBinding = EamxSplashActivityBinding.inflate(layoutInflater)
        EAMXCUMySharedPreferences(this)
        //setContentView(mBinding.root)
        initSecondaryFirebaseInstance()
        checkUpdateApp()

    }

    private fun checkUpdateApp() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                appUpdateManager.registerListener(this@EAMXSplashActivity)
                //val forceVersion = true
                val forceVersion = withContext(Dispatchers.IO) {
                    RemoteConfigFirebase.validateForceVersion()
                }
                val updateInfoTask = appUpdateManager.appUpdateInfo
                val updateInfo = withContext(Dispatchers.IO) { Tasks.await(updateInfoTask) }
                if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    appUpdateManager.startUpdateFlowForResult(
                        updateInfo,
                        if (forceVersion && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE,
                        this@EAMXSplashActivity,
                        if (forceVersion && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) codeResultUpdateForce else codeResultUpdate
                    )
                } else openSession()
            } catch (_: Exception){
                openSession()
            }
        }
    }

    private fun reloadActivity() {
        startActivity(Intent(this,this::class.java))
        finish()
    }

    private fun openSession() {
        if (!(eamxcu_preferences.getData(
                EAMXEnumUser.SESSION.name,
                EAMXTypeObject.BOOLEAN_OBJECT
            ) as Boolean)
        ) {
            showLoggin()
        } else if (eamxcu_preferences.getData(
                EAMXEnumUser.SKIP.name,
                EAMXTypeObject.BOOLEAN_OBJECT
            ) as Boolean
        ) {
            startActivity(Intent(applicationContext, EAMXHomeActivity::class.java))
            finish()
        } else {
            startActivity(Intent(applicationContext, EAMXInicioActivity::class.java))
            finish()
        }
    }

    private fun showLoggin() {
        startActivityForResult(
            Intent(this, EAMXLoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), EAMXEnums.CONFIRMATED.code
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        eamxLog("Req: $requestCode result: $resultCode $data")
        if (requestCode == codeResultUpdate && resultCode != RESULT_OK) {
            openSession()
            return
        }
        if (requestCode == codeResultUpdateForce && resultCode != RESULT_OK) {
            openPlaystore()
            return
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EAMXEnums.CONFIRMATED.code) {
                data?.let {
                    when (data.getBooleanExtra(EAMXEnums.CONFIRMATED.name, false)) {
                        true -> {
                            eamxLog("response", "Bienvenido ${data.getStringExtra("name")}")
                            eamxLog("response", "Bienvenido $data")
                            startActivity(
                                Intent(this, EAMXInicioActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            )
                            eamxcu_preferences.saveData(EAMXEnumUser.SESSION.name, true)
                            finish()
                            return
                        }
                        false -> {
                            showLoggin()
                            return
                        }
                    }
                }
            }
        } else {
            finish()
        }
    }

    private fun initSecondaryFirebaseInstance() {
        //This builder may call more functions depending on features being used
        val secondProjectOptions = FirebaseOptions.Builder()
            .setProjectId(ConstansApp.appProjectId())
            .setStorageBucket(ConstansApp.appStorageBucket())
            .setApplicationId(ConstansApp.appId())
            .setApiKey(ConstansApp.appIdKey())
            .build()

        /**
         * @throws IllegalStateException if an app with the same name has already been initialized.
         */
        try {
            FirebaseApp.initializeApp(
                this,
                secondProjectOptions,
                ConstansApp.appName()
            )
        } catch (e: IllegalStateException) {
            // The instance has al ready been initialized.
        }
    }

    private fun openPlaystore(){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }finally {
            finish()
        }
    }

    override fun onStateUpdate(p0: InstallState) {
        when (p0.installStatus) {
            InstallStatus.DOWNLOADING -> {
                progresDialog.show(supportFragmentManager)
                eamxLog("P: ${p0.bytesDownloaded}, T: ${p0.totalBytesToDownload}, K: ${(p0.bytesDownloaded * 100 / p0.totalBytesToDownload)}")
                progresDialog.setProgress((p0.bytesDownloaded * 100 / p0.totalBytesToDownload).toInt())
            }
            InstallStatus.DOWNLOADED -> {
                if (supportFragmentManager.findFragmentByTag(progresDialog::class.simpleName) != null) {
                    progresDialog.dismissAllowingStateLoss()
                }
                appUpdateManager.completeUpdate()
            }
            InstallStatus.INSTALLING -> finish()
            InstallStatus.FAILED -> {
                eamxLog("${p0.installErrorCode}")
                openPlaystore()
            }
            else -> {
                eamxLog(p0.installStatus.toString())
            }
        }
    }


}