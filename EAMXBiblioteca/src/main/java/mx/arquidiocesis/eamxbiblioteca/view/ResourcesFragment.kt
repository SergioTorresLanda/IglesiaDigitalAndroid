package mx.arquidiocesis.eamxbiblioteca.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.resources_fragment.*
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.adapters.AdapterResources
import mx.arquidiocesis.eamxbiblioteca.models.ResourcesModel
import mx.arquidiocesis.eamxcommonutils.util.openYoutubeApi

class ResourcesFragment(private val list: List<ResourcesModel>) : Fragment() {
    var boolean = false
    private var videosList: MutableList<ResourcesModel> = mutableListOf()
    private var documentosList: MutableList<ResourcesModel> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.resources_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        list.forEach {
            when (it.type) {
                "VIDEO" -> {
                    videosList.add(it)
                }
                else -> {
                    documentosList.add(it)
                }
            }
        }
        if(documentosList.size>0){
            tvDocumento.visibility=View.VISIBLE
            rvDocumento.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                adapter = AdapterResources(documentosList, requireContext(), 1) {
                    openYoutubeApi(it.url)
                }
            }
        }
        if (videosList.size > 0) {
            tvVer.visibility = View.VISIBLE
            tvVideos.visibility = View.VISIBLE
            rvVideos.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = AdapterResources(videosList, requireContext(), 2) {

                }
            }
            tvVer.setOnClickListener {
                if (!boolean) {
                    tvVer.text = "Ver menos"
                    boolean = true
                    rvVideos.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = AdapterResources(videosList, requireContext(), 3) {

                        }
                    }
                } else {
                    tvVer.text = "Ver todo"
                    boolean = false
                    rvVideos.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = AdapterResources(videosList, requireContext(), 2) {

                        }
                    }
                }
            }
        }


    }

}


