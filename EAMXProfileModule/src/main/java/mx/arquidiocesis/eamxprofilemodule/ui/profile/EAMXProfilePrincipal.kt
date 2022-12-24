package mx.arquidiocesis.eamxprofilemodule.ui.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.TYPE_ALERT_MULTI_SELECTION
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.imagen.FunImagen
import mx.arquidiocesis.eamxcommonutils.util.imagen.ImagenProfile
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePrincipalFragmentBinding
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.ui.admin.AdminModulesFragment
import mx.arquidiocesis.eamxprofilemodule.ui.profile.model.EAMXViewPagerConstructor
import mx.arquidiocesis.eamxprofilemodule.viewmodel.ManagerProfileViewModel
import mx.arquidiocesis.eamxprofilemodule.viewmodel.ManagerProfileViewModel.Companion.SELECT_CAMERA
import mx.arquidiocesis.eamxprofilemodule.viewmodel.ManagerProfileViewModel.Companion.SELECT_GALLERY
import java.io.ByteArrayOutputStream


const val CAMERA_GALLERY = 10002
const val PERMISSION_CAMERA = 10004
const val PERMISSION_GALLERY = 10005
const val PERMISSION_STORAGE = 10006
const val PERMISSION_LOCATION = 10007

class EAMXProfilePrincipalFragment :
    EAMXBaseFragment<EAMXProfilePrincipalFragment.NavigationActivity, EamxProfilePrincipalFragmentBinding>(
        R.layout.eamx_profile_principal_fragment
    ) {

    interface NavigationActivity : EAMXBaseFragment.NavigationActivity {
        fun launchCamera(cb: (Uri?) -> Unit)
    }

    private val imageProfileViewModel: ManagerProfileViewModel by lazy {
        getViewModel {
            ManagerProfileViewModel(RepositoryProfile(requireContext()))
        }
    }

    private val logOutViewModel: EAMXProfilePrincipalFragmentViewModel by lazy {
        getViewModel {
            EAMXProfilePrincipalFragmentViewModel()
        }
    }

    private val dialogRemoveAccount by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Aviso")
            .setMessage("Al eliminar tu cuenta, se borrará tu usuario y no tendrás más acceso a la información, servicios y publicaciones realizadas desde la misma")
            .setPositiveButton("Eliminar") { _, _ ->
                showLoader()
                imageProfileViewModel.deleteUser{
                    hideLoader()
                    if (it) (requireActivity() as EAMXSignOut).signOut(true)
                }
            }.setNegativeButton("Cancelar") { _, _ ->

            }.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

    }

    companion object {
        @JvmStatic
        fun newInstance() = EAMXProfilePrincipalFragment()
    }

    override fun initView() {
        activity?.window?.statusBarColor = context?.getColor(R.color.primaryColor)!!
        activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)


        (requireActivity() as EAMXHome).showToolbar(false, AppMyConstants.perfil)
        logOutViewModel.initFragments(
            requireContext(),
            EAMXViewPagerConstructor(requireActivity().supportFragmentManager, lifecycle),
            (requireActivity() as EAMXSignOut),
            (requireActivity() as EAMXHome)
        ) { item ->
            val name = item.name ?: ""
            val middleName = item.first_surname ?: ""
            val lastName = item.second_surname ?: ""
            val nameComplete = "$name $lastName $middleName"

            vBind.apply {

                if (nameComplete.trim().isNotEmpty()) {
                    eamxProfileContentFragmentUserName.text = nameComplete
                }
                item.uri?.let { urlWeb ->
                    if (urlWeb.isNotEmpty()) {
                        loadImage(url = urlWeb)
                    }
                }
            }
        }

        vBind.apply {
            eamxcu_preferences.apply {
                val name = getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT)
                val lastName =
                    getData(EAMXEnumUser.USER_LAST_NAME.name, EAMXTypeObject.STRING_OBJECT)
                val middleName =
                    getData(EAMXEnumUser.USER_MIDDLE_NAME.name, EAMXTypeObject.STRING_OBJECT)
                val nameComplete = "$name $lastName $middleName"

                eamxProfileContentFragmentUserName.text = nameComplete
                ImagenProfile().loadImageProfile(circleUserImageProfile, requireContext())
            }

            imgSettings.setOnClickListener {
                val newFragment: Fragment = AdminModulesFragment.newInstance()
                val transaction: FragmentTransaction =
                    requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFragmentProfile, newFragment)
                transaction.addToBackStack("")
                transaction.commit()
                viewPagerInfo.adapter = null
            }
            eamxRemoveAccount.setOnClickListener {
                dialogRemoveAccount.show()
            }
            imgCamera.setOnClickListener {
                UtilAlert.Builder()
                    .setTitle(getString(R.string.message_select_option))
                    .setTypeAlert(TYPE_ALERT_MULTI_SELECTION)
                    .setOptions(arrayListOf(SELECT_CAMERA, SELECT_GALLERY))
                    .setListener { item ->
                        when (item as String) {
                            SELECT_CAMERA -> {
                                if (UtilValidPermission().validListPermissionsAndBuildRequest(
                                        this@EAMXProfilePrincipalFragment, arrayListOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ), PERMISSION_CAMERA
                                    )
                                ) onLaunchPickerCamera()
                            }
                            SELECT_GALLERY -> {
                                if (UtilValidPermission().validListPermissionsAndBuildRequest(
                                        this@EAMXProfilePrincipalFragment,
                                        arrayListOf(
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        ),
                                        PERMISSION_GALLERY

                                    )
                                ) {
                                    launchGallery()
                                }
                            }
                        }
                    }
                    .build()
                    .show(childFragmentManager, "")
            }
            viewPagerInfo.adapter = logOutViewModel.adapter

            TabLayoutMediator(tabLayout, viewPagerInfo) { tab, position ->
                tab.text = logOutViewModel.adapter?.getTitleForPosition(position)
            }.attach()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && CAMERA_GALLERY == requestCode) data?.data.uploadImage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (UtilValidPermission().allPermissionsAreAgree(grantResults)) {
            when (requestCode) {
                PERMISSION_CAMERA -> onLaunchPickerCamera()
                PERMISSION_GALLERY -> this.launchGallery()
            }
        } else {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage("Debes otorgar el permiso para el uso de la cámara o la galería para poder agregar tu imagen de perfil, además del permiso de fotos y videos")
                .setTextButtonOk("Ir a la configuración")
                .setListener {
                    startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + context?.packageName))
                    )
                }
                .build()
                .show(childFragmentManager, "")
        }
    }

    private fun initObservers() {
        imageProfileViewModel.responseUploadImage.observe(viewLifecycleOwner) {
            hideLoader()
            if (!it.isNullOrEmpty()) {
                loadImage(it)
                toast("La fotografía de perfil se actualizo correctamente")
            }
        }

        imageProfileViewModel.responseError.observe(viewLifecycleOwner) {
            hideLoader()
            toast(it)
        }
    }

    private fun Uri?.uploadImage() {
        try {
            val contentResolver = context?.contentResolver
            if (this == null || contentResolver == null) throw java.lang.Exception()
            val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                MediaStore.Images.Media.getBitmap(contentResolver, this);
            } else {
                val source = ImageDecoder.createSource(contentResolver, this)
                ImageDecoder.decodeBitmap(source)
            }
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val base64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
            if (base64.isNullOrEmpty()) throw java.lang.Exception()
            showLoader()
            imageProfileViewModel.buildFileByUploadImage(base64)
        } catch (e: Exception) {
            toast("Ocurrio un error con tu imagen selecciona otra")
        }
    }

    private fun loadImage(url: String = "") {
        val name = eamxcu_preferences.getData(
            EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name,
            EAMXTypeObject.STRING_OBJECT
        ).toString()
        FunImagen().updateImage(requireContext(), url, name) {
            if (it) {
                vBind.circleUserImageProfile.setImageBitmap(
                    BitmapFactory.decodeFile(
                        FunImagen().imagenInterna(
                            requireContext(),
                            name
                        ).absolutePath
                    )
                )
            } else {
                vBind.circleUserImageProfile.setImageResource(R.drawable.user)
            }
        }
    }

    private fun onLaunchPickerCamera() = handleActivity?.launchCamera { it.uploadImage() }

    private fun launchGallery() = this.startActivityForResult(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }, CAMERA_GALLERY
    )
}