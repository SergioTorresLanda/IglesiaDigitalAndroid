package mx.arquidiocesis.eamxredsocialmodule.news.create

import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXTypeValidation
import mx.arquidiocesis.eamxcommonutils.application.validation.EAMXValidationModel
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxDialogClosePostBinding
import mx.arquidiocesis.eamxredsocialmodule.news.create.EAMXCreateFragment.Companion.REQUEST_PICK_MEDIA
import mx.arquidiocesis.eamxredsocialmodule.news.create.adapter.MediaPickerAdapter
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCreatePostRequest
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia
import mx.arquidiocesis.eamxredsocialmodule.news.create.update_media.EAMXUpdateMediaFragment
import mx.arquidiocesis.eamxredsocialmodule.news.create.utils.FileHelper
import java.io.File


fun EAMXCreateFragment.launchMediaPicker() {
    FileHelper.checkPermissions(context) {
        //FileHelper.pickMultipleMedia(this, REQUEST_PICK_MEDIA)
    }
}

fun EAMXCreateFragment.setupRecyclerView(recycler: RecyclerView, replaceAdapter: Boolean) {
    //recycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    //adapter = if (replaceAdapter) MediaPickerAdapter(height = recycler.height) else adapter
    //recycler.adapter = adapter


    adapter.onItemClickListener = {
        //todo removiendo observer checar si controlando el backStack no se crea 2 o mas veces
        viewModelEAMX.validationDataActionFromActivity.removeObservers(this)
       /* eamxBackHandler.changeFragment(
            EAMXUpdateMediaFragment.newInstance(
                adapter
            ), R.id.contentFragmentRedSocial, EAMXUpdateMediaFragment::class.java.simpleName
        )*/

//        requireActivity().supportFragmentManager.commit {
//            replace(R.id.contentFragmentRedSocial, EAMXUpdateMediaFragment.newInstance(callback, callbackBottom, adapter.items))
//            addToBackStack(EAMXUpdateMediaFragment::class.java.simpleName)
//        }
    }
}

fun EAMXCreateFragment.createPost(files: ArrayList<ClipData.Item>, viewModel: EAMXCreateViewModel) {
    val arrayListValidations: ArrayList<EAMXValidationModel> = ArrayList()

    vBind.root.context.apply {
        val content = vBind.etComment.text.toString()
        arrayListValidations.add(
            EAMXValidationModel(
                vBind.etComment,
                EAMXTypeValidation.NOT_EMPTY,
                true,
                getString(R.string.enter_content)
            )
        )
      //  val filesMultimedia = files.map { getFileMetaData(this, it.uri)!! }

        //TODO  REPLACE HARD CODE FIELDS asParam, feeling,location,status,FIIDEMPLEADO,as
       /* viewModel.requestValidationRegister(
            EAMXCreatePostRequest(
                "User", 20, content, null, filesMultimedia,
                null, "Public", 100, "Group"
            ), arrayListValidations
        )*/
    }
}

fun EAMXCreateFragment.showCancelDialog(
    onContinueListener: () -> Unit,
    onDiscardListener: () -> Unit,
) {

    context?.let {
        val dialogView = layoutInflater?.inflate(R.layout.eamx_dialog_close_post, null, false)

        val dialog = MaterialAlertDialogBuilder(it, R.style.AlertDialogTheme).setView(dialogView)
            .setCancelable(false).show()

        dialogView?.let { dialogViewBinding ->
            val dialogBinding = EamxDialogClosePostBinding.bind(dialogViewBinding)
            dialogBinding.tvContinue.setOnClickListener {
                dialog.dismiss()
                onContinueListener()
            }
            dialogBinding.tvClose.setOnClickListener {
                eamxBackHandler.hideKeyBoard()
                dialog.dismiss()
                onDiscardListener()
            }
        }
    }
}



