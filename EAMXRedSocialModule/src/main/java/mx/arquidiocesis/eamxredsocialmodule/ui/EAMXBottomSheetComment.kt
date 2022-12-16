package mx.arquidiocesis.eamxredsocialmodule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.loader.UtilLoader
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxCreateCommentFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.CommentModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultMultiProfileModel
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia
import mx.arquidiocesis.eamxredsocialmodule.news.create.utils.GetPerfilImagen
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel


class EAMXBottomSheetComment(
    val idPost: Int,
    var model: MutableLiveData<CommentModel?>,
    var list: List<ResultMultiProfileModel>? = null,
    val repuesta: Int? = null,
    val listener: (Boolean) -> Unit
) : BottomSheetDialogFragment() {
    lateinit var binding: EamxCreateCommentFragmentBinding
    lateinit var viewModel: RedSocialViewModel
    var edit = false
    var idUser = 0
    var scope = 1
    private val loader by lazy { UtilLoader() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = RedSocialViewModel(Repository(requireContext()))
        initObservers()
        binding = EamxCreateCommentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    fun initView() {
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
        binding.apply {
            GetPerfilImagen().loadImageProfile(
                requireContext(),
                eamxcu_preferences.getData(
                    EAMXEnumUser.URL_PICTURE_PROFILE_USER.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String, ivUser
            )
            model.value?.let { m ->
                edit = true
                etComment.setText(m.content)
                m.scope.id?.let { s ->
                    tvUserName.text = m.scope.name
                } ?: run { tvUserName.text = nameCompleted }
            } ?: run {
                if (!list.isNullOrEmpty()) {
                    spNameUser.visibility = View.VISIBLE
                    val lista: ArrayList<String> = ArrayList()
                    lista.add(nameCompleted)
                    list!!.forEach {
                        it.name?.let{n->
                            lista.add(n)
                        }?: run {
                            lista.add(" ")
                        }
                    }
                    val adapterSpinner = ArrayAdapter(
                        requireContext(),
                        R.layout.custom_spinner_red, lista
                    )
                    tvUserName.visibility = View.INVISIBLE
                    spNameUser.adapter = adapterSpinner
                } else {
                    tvUserName.text = nameCompleted
                }
            }
            btnPublicar.setOnClickListener {
                if (!etComment.text.toString().isNullOrEmpty()) {
                    showLoader()
                    getIdProfile()
                    if (edit) {
                        update()
                    } else {
                        post()
                    }

                } else {
                    UtilAlert
                        .Builder()
                        .setTitle("Aviso")
                        .setMessage("Ingrese un mensaje para compartir")
                        .build()
                        .show(childFragmentManager, "")
                }
            }
            btnCancel.setOnClickListener {
                dismiss()
            }

        }
    }

    private fun post() {
        viewModel.setComment(
            idPost = idPost,
            comment = binding.etComment.text.toString(),
            scope = scope,
            idUser = idUser,
            idComent = repuesta
        )
    }

    private fun update() {
        viewModel.updateComment(
            idCommen = model.value!!.id,
            comment = binding.etComment.text.toString()
        )
    }

    fun getIdProfile() {
        binding.apply {
            if (edit) {
                model.value!!.scope.id?.let {
                    idUser = it
                    scope = if (model.value!!.scope.typeId != null) {
                        model.value!!.scope.typeId!!
                    } else {
                        1
                    }
                }
            } else {
                if (spNameUser.visibility == View.VISIBLE) {
                    val select = spNameUser.selectedItemPosition
                    if (select > 0) {
                        val item = list!![select - 1]
                        idUser = item.id
                        scope = item.type.toInt() + 1
                    } else {
                        idUser = 0
                    }
                }
            }

        }
    }

    fun initObservers() {

        viewModel.responseCommentPost.observe(viewLifecycleOwner) {
            listener(true)
            dismiss()
        }
        viewModel.error.observe(viewLifecycleOwner) {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
                .setListener { dismiss() }
                .build()
                .show(childFragmentManager, "")
        }
        viewModel.errorBottom.observe(viewLifecycleOwner) {
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setIsCancel(false)
                .setListener { dismiss() }
                .build()
                .show(childFragmentManager, "")
        }

    }

    fun showLoader(tag: String = "") {
        if (!loader.isAdded) {
            loader.show(childFragmentManager, tag)
        }
    }

    fun hideLoader() {
        if (loader.isAdded) {
            loader.dismissAllowingStateLoss()
        }
    }

}