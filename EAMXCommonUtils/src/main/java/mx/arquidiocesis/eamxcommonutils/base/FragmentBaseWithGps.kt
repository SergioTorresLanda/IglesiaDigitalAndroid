package mx.arquidiocesis.eamxcommonutils.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_ACCEPT
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CANCEL
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CLOSE
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxcommonutils.util.permission.MULTIPLE_PERMISSIONS
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission


const val CODE_RESULT_GPS = 299
private const val MAX_NUMBER_REQUEST = 3

open class FragmentBaseWithGps: Fragment(), EAMXHome {

    private val TAG_BASE = "FragmentBaseWithGps"
    private var NUMBER_REPEAT = 1
    private val loader by lazy { UtilLoader() }
    protected lateinit var callBack: EAMXHome
    private lateinit var listener : () -> Unit

    protected fun showLoader(tag: String = "") {
        if (!loader.isAdded) {
            loader.show(childFragmentManager, tag)
        }
    }

    protected fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }

    protected fun validationPermissions(listener: () -> Unit) {
        this.listener =  listener
        validationPermissionsWithOutListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS ->
                when {
                    grantResults.isNotEmpty() &&
                            grantResults.filter { it == PackageManager.PERMISSION_GRANTED }.size == grantResults.size -> {
                        validationGps()
                    }
                    MAX_NUMBER_REQUEST > NUMBER_REPEAT -> {
                        NUMBER_REPEAT++
                        launchMessagePermissionsNotAllow()
                    }
                    else -> {
                        launchMessageGotoSettings()
                    }
                }
        }
    }

    private fun validationPermissionsWithOutListener(){
        if(UtilValidPermission().validListPermissionsAndBuildRequest(
                this,
                arrayListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )){
            validationGps()
        }
    }

    private fun validationGps(){
        if(UtilValidPermission().isGpsAvailableInDevice(
                requireActivity(),
                Context.LOCATION_SERVICE
            )) {
            this.listener()
        }else{
            launchMessageGpsNotAvailable()
        }
    }

    private fun launchMessageGpsNotAvailable(){
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.text_message_available_gps))
            .setTextButtonOk(getString(R.string.button_message_available_gps))
            .setTextButtonCancel(getString(R.string.text_button_cancel))
            .setListener { response ->
                when (response) {
                    ACTION_ACCEPT -> {
                        startActivityForResult(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            CODE_RESULT_GPS
                        )
                    }
                    ACTION_CANCEL, ACTION_CLOSE -> back()
                    else -> back()
                }
            }
            .build()
            .show(childFragmentManager, TAG_BASE)
    }

    private fun launchMessagePermissionsNotAllow(){
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.text_message_permissions_not_available))
            .setTextButtonOk(getString(R.string.button_message_permissions_not_available))
            .setTextButtonCancel(getString(R.string.text_button_cancel))
            .setListener { response ->
                when (response) {
                    ACTION_ACCEPT -> {
                        validationPermissionsWithOutListener()
                    }
                    ACTION_CANCEL, ACTION_CLOSE -> back()
                    else -> back()
                }
            }
            .build()
            .show(childFragmentManager, TAG_BASE)
    }

    private fun launchMessageGotoSettings(){
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.text_message_go_to_settings))
            .setTextButtonOk(getString(R.string.button_message_go_to_settings))
            .setListener { response ->
                when (response) {
                    ACTION_ACCEPT -> {
                        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = uri }
                        startActivityForResult(intent, CODE_RESULT_GPS)
                    }
                    ACTION_CANCEL, ACTION_CLOSE -> back()
                    else -> back()
                }
            }
            .setIsCancel(false)
            .build()
            .show(childFragmentManager, TAG_BASE)
    }

    private fun back(){
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .build().back()
    }


    override fun postListener(activate: Boolean) {
    }

    override fun restoreToolbar() {
    }

    override fun showToolbar(boolean: Boolean, title: String) {
    }

    override fun showToolbar(
        toolbarShow: Boolean,
        titleFragment: String,
        onActionClickListener: () -> Unit
    ) {

    }

    override fun showToolbar(
        toolbarShow: Boolean,
        titleFragment: String,
        actionText: String,
        tryGoBackListener: (goBackEvent: (Boolean) -> Unit) -> Unit,
        onActionClickListener: () -> Unit
    ) {
    }
}