package mx.arquidiocesis.eamxredsocialmodule.news.create.update_media

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.MediaAdapter
import mx.arquidiocesis.eamxredsocialmodule.adapter.UpdateAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxEditMediaFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia
import mx.arquidiocesis.eamxredsocialmodule.news.create.update_media.adapter.MediaUpdateAdapter
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel

class EAMXUpdateMediaFragment : DialogFragment() {

    companion object {
        const val REQUEST_UPDATE_MEDIA = 102
        const val REQUEST_ADD_MEDIA = 103
        const val TAG = "EAMXUpdateMediaFragment"
        fun newInstance(adapterPrincpal: MediaAdapter,del: MutableLiveData<MutableList<EAMXMultimedia>>): EAMXUpdateMediaFragment {
            val args = Bundle()
            val fragment = EAMXUpdateMediaFragment()
            fragment.adapterPrincpal = adapterPrincpal
            fragment.media = adapterPrincpal.items
            fragment.arguments = args
            fragment.del=del
            return fragment
        }
    }

    lateinit var vBind: EamxEditMediaFragmentBinding
    lateinit var adapter: UpdateAdapter
    lateinit var adapterPrincpal: MediaAdapter
    lateinit var viewmodel: RedSocialViewModel
    lateinit var del:MutableLiveData<MutableList<EAMXMultimedia>>
    var media = mutableListOf<EAMXMultimedia>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBind = DataBindingUtil.inflate(
            inflater,
            R.layout.eamx_edit_media_fragment,
            container,
            false
        )
        return vBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = RedSocialViewModel(Repository(requireContext()))
        setupRecyclerView(vBind.rvMedia)
        vBind.tvAddMedia.setOnClickListener {
            selectSingleMedia(REQUEST_ADD_MEDIA)
        }

    }

//    override fun initView() {

//        media = intent.extras!!["data"] as ArrayList<ClipData.Item>
//        val gson = Gson()
//
//        val listString = intent.getStringExtra("data")
//        val list = gson.fromJson(listString, JsonArray::class.java)
//        for (jsonElement in list.asJsonArray) {
//            media.add(gson.fromJson(jsonElement, ClipData.Item::class.java))
//        }
//        for (uri in listString!!){
//            media.add(ClipData.Item(Uri.parse(uri!!)))
//        }
//        media.addAll(listString!!.map { ClipData.Item(it) })
//        vBind.btnBackWhiteCancel.setOnClickListener { finish() }
//        setupRecyclerView(vBind.rvMedia)
//
//        vBind.tvAddMedia.setOnClickListener {
//            selectSingleMedia(REQUEST_ADD_MEDIA)
//        }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // val array = adapter.items
            var item: EAMXMultimedia? = null
            val uriString = data?.data
            if (data?.clipData == null) {
                item = viewmodel.getFileMetaData(ClipData.Item(uriString).uri)
            } else {
                val selectedMediaUri = data.clipData
                selectedMediaUri?.let { sel ->
                    item = viewmodel.getFileMetaData(sel.getItemAt(0).uri)
                }

                // for (i in 0 until selectedMediaUri!!.itemCount) { }

            }
            item?.let {i->
                when (requestCode) {
                    REQUEST_ADD_MEDIA -> {
                        adapter.items.add(i)
                        adapter.notifyDataSetChanged()
                        adapterPrincpal.items = adapter.items
                        adapterPrincpal.notifyDataSetChanged()

                    }
                    REQUEST_UPDATE_MEDIA -> {
                        adapter.items[adapter.indexToUpdate] = i
                        adapter.notifyItemChanged(adapter.indexToUpdate)
                        adapterPrincpal.items[adapter.indexToUpdate] = i
                        adapterPrincpal.notifyItemChanged(adapter.indexToUpdate)
                    }
                }
            } ?: toast("Ocurrió un error al recuperar el archivo")


        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }


}