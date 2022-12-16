package mx.arquidiocesis.eamxredsocialmodule.news.create


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXStatusValidation
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxCreateFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemMediaPickerBinding
import mx.arquidiocesis.eamxredsocialmodule.news.create.adapter.MediaPickerAdapter
import mx.arquidiocesis.eamxredsocialmodule.news.create.utils.GetPerfilImagen


class EAMXCreateFragment : EAMXBaseFragment() {

    lateinit var vBind: EamxCreateFragmentBinding
    lateinit var viewModelEAMX: EAMXCreateViewModel
    var loadItems = ArrayList<ClipData.Item>()
    lateinit var adapter: MediaPickerAdapter
    private var isSending = false

    override fun getLayout(): Int = R.layout.eamx_create_fragment

    override fun initBinding(view: View) {
        vBind = EamxCreateFragmentBinding.bind(view)
    }

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun setViewModel() {
        viewModelEAMX = ViewModelProvider(this).get(EAMXCreateViewModel::class.java)
    }

    override fun initObservers() {
        viewModelEAMX.validationDataActionFromActivity.observe(this, {
            when (it.statusValidation) {
                EAMXStatusValidation.CORRECT -> {
                    if (vBind.etComment.text.toString().trim().isEmpty()) {
                        toast("Ingrese un mensaje para compartir")
                        return@observe
                    }
                    if (isSending) return@observe
                    isSending = true
                    viewModelEAMX.attemptCreatePost(context, it.request)
                }
                EAMXStatusValidation.INCORRECT -> toast(it.errorMessage)
            }
        })
        viewModelEAMX.responseGeneric.observe(this, {
            when (it.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
                    eamxBackHandler.showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
                    isSending = false
                    hideProgressBarCustom()

                    eamxBackHandler.hideProgressBarCustom()
                    toast("Publicación creada exitosamente")
                    activity?.onBackPressed()
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    isSending = false
                    hideProgressBarCustom()
                    it.errorData?.let { errorMessage ->
                        when (errorMessage) {
                            EAMXCreateViewModel.ERROR_SEND_MULTIMEDIA -> {
                                toast(getString(R.string.error_send_multimedia))
                            }
                            else -> toast(errorMessage)
                        }
                    }
                }
                EAMXStatusRequestEnum.NONE -> {
                    isSending = false
                    hideProgressBarCustom()
                }
            }
        })
    }

    override fun initView(view: View) {


        val name =
            eamxcu_preferences.getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT)
                .toString()
        val lastName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_LAST_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val middleName = eamxcu_preferences.getData(
            EAMXEnumUser.USER_MIDDLE_NAME.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String
        val nameCompleted = "$name $lastName $middleName"
        vBind.apply {
            tvUserName.text = nameCompleted
            GetPerfilImagen().loadImageProfile(requireContext(),
                eamxcu_preferences.getData(
                    EAMXEnumUser.URL_PICTURE_PROFILE_USER.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String,ivUser
            )
            /*callback.showToolbar(true, "Crear publicación", "Publicar",
                { goBackEvent ->
                    if (vBind.etComment.text.isEmpty() && adapter.items.isEmpty()) {
                        goBackEvent(true)
                        callbackBottom.showBottom()
                    } else {
                        showCancelDialog({

                        }, {
                            goBackEvent(true)
                            callbackBottom.showBottom()
                        })
                    }
                },
                {
                    createPost(adapter.items, viewModelEAMX)
                }
            )*/

            vBind.tvAddImagen.setOnClickListener { launchMediaPicker() }

            //vBind.etComment.doOnTextChanged { _, _, _, count -> callback.postListener(count > 0) }

        }

//        vBind.cvAddMedia.setOnClickListener { createPost(adapter.items, viewModelEAMX) }

        setupRecyclerView(vBind.rvMedia, !::adapter.isInitialized)

        /*handlerActivity.actionOnBackPressed {
            return@actionOnBackPressed vBind.etComment.length() != 0 || adapter.items.isNotEmpty()
        }*/

        vBind.root.viewTreeObserver.addOnGlobalLayoutListener {
            if (adapter.height == 0) {
                adapter.items.addAll(loadItems)
                adapter.height = vBind.rvMedia.height
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //handlerActivity = context as EAMXComunicationCreateFragment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val array = adapter.items
        if (resultCode == RESULT_OK) {
            val uriString = data?.data
            if (data?.clipData == null) {
                val item = ClipData.Item(uriString)
                array.add(item)
            } else {
                val selectedMediaUri = data.clipData
                for (i in 0 until selectedMediaUri!!.itemCount) {
                    val item = selectedMediaUri.getItemAt(i)
                    array.add(item)
                }
            }

            adapter.height = vBind.rvMedia.height
            adapter.notifyDataSetChanged()
            if (adapter.items.size == 1) {

                vBind.itemImage.visibility = View.VISIBLE
                vBind.rvMedia.visibility = View.GONE

                Glide
                    .with(this)
                    .asBitmap()
                    .load(adapter.items[0].uri)
                    .into(vBind.itemImage)

                //val meta = getFileMetaData(context, adapter.items[0].uri)
               /* if(meta?.isImage() == false)
                    vBind.ivPlay.visibility = View.VISIBLE
*/

            }else{
                vBind.itemImage.visibility = View.GONE
                vBind.ivPlay.visibility = View.GONE
                vBind.rvMedia.visibility = View.VISIBLE
            }
        }
    }


    companion object {
        const val REQUEST_PICK_MEDIA = 101
        fun newInstance(
            items: ArrayList<ClipData.Item> = ArrayList()
        ): EAMXCreateFragment {
            val args = Bundle()
            val fragment = EAMXCreateFragment()
            fragment.loadItems = items
            fragment.arguments = args
            return fragment
        }
    }
}
