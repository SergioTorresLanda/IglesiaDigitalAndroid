package mx.arquidiocesis.eamxredsocialmodule.news.create.update_media

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxredsocialmodule.adapter.UpdateAdapter
import mx.arquidiocesis.eamxredsocialmodule.news.create.update_media.EAMXUpdateMediaFragment.Companion.REQUEST_UPDATE_MEDIA
import mx.arquidiocesis.eamxredsocialmodule.news.create.utils.FileHelper
import mx.arquidiocesis.eamxredsocialmodule.ui.EDITAR
import mx.arquidiocesis.eamxredsocialmodule.ui.ELIMINAR

fun EAMXUpdateMediaFragment.setupRecyclerView(recycler: RecyclerView) {
    recycler.layoutManager = LinearLayoutManager(context)
    recycler.itemAnimator = DefaultItemAnimator()

    adapter = UpdateAdapter(media, recycler) { Etiqueta, item ->
        when (Etiqueta) {
            EDITAR -> {
                val pos = adapter.items.indexOf(item)
                adapter.indexToUpdate = pos
                selectSingleMedia(REQUEST_UPDATE_MEDIA)
            }
            ELIMINAR -> {
                val pos = adapter.items.indexOf(item)
                adapter.items.remove(item)
                adapter.notifyItemRemoved(pos)
                item.id?.let {
                    del.value?.let { d->
                        d.add(item)
                    }
                }
                adapterPrincpal.items.remove(item)
                adapterPrincpal.notifyItemRemoved(pos)
            }
            "" -> {
              //  val intent = Intent()
                //intent.action = Intent.ACTION_VIEW
                //intent.setDataAndType(Uri.fromFile(File(item.path!!)), "image/* video/*")
                //requireActivity().startActivity(intent)
            }

        }
    }
    recycler.adapter = adapter
}

fun EAMXUpdateMediaFragment.selectSingleMedia(code: Int) {
    FileHelper.checkPermissions(context) {
        FileHelper.pickSingleMedia(this, code)
    }
}

/*fun EAMXUpdateMediaFragment.onDoneClick() {
    eamxBackHandler.changeFragment(
        EAMXCreateFragment.newInstance(
            adapter.items
        ), R.id.contentFragmentRedSocial, EAMXCreateFragment::class.java.simpleName
    )
}*/
