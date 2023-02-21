package mx.arquidiocesis.eamxcommonutils.multimedia

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import es.voghdev.pdfviewpager.library.PDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import es.voghdev.pdfviewpager.library.remote.DownloadFileUrlConnectionImpl
import es.voghdev.pdfviewpager.library.util.FileUtil
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.customui.alert.ProgressDialog
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPdfBinding
import java.io.File
import java.util.*

class EAMXPdfFragment : Fragment(), DownloadFile.Listener {
    lateinit var binding: FragmentPdfBinding
    private var adapter: PDFPagerAdapter? = null
    private val dialogError by lazy {
        UtilAlert
            .Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage("Ocurrio un error al intentar cargar el recurso")
            .setListener {
                activity?.onBackPressed()
            }
            .setIsCancel(false)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("pdf")?.let(::loadPDf)
    }

    private fun loadPDf(pdfUrl: String) {
        val file = File(context?.cacheDir, FileUtil.extractFileNameFromURL(pdfUrl))
        if (file.exists()) {
            if (file.lastModified() != 0L) {
                val filesModifiedDate = Calendar.getInstance()
                filesModifiedDate.timeInMillis = file.lastModified()
                val currentTime = Calendar.getInstance()
                currentTime.add(Calendar.WEEK_OF_MONTH, -1)
                //si el archivo lleva guardado menos de una semana lo muestra si no lo descarga
                if (filesModifiedDate >= currentTime) {
                    addPdfView(file.absolutePath)
                    return
                }
            }
        }
        dialogProgress.show(childFragmentManager)
        DownloadFileUrlConnectionImpl(context, Handler(), this)
            .download(pdfUrl, file.absolutePath)
        file.setLastModified(Calendar.getInstance().timeInMillis)
    }

    private fun addPdfView(path: String) {
        if (context == null) return
        val pdfViewPager = PDFViewPager(requireContext(), path)
        adapter = PDFPagerAdapter(requireContext(), path)
        pdfViewPager.adapter = this.adapter
        addView(pdfViewPager)
    }

    private fun addView(view: View) {
        binding.lPdf.removeAllViews()
        binding.lPdf.addView(
            view, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

    }

    private val dialogProgress by lazy {
        ProgressDialog()
            .setOnCancel {
                it.dismiss()
                activity?.onBackPressed()
            }
    }

    override fun onSuccess(url: String?, destinationPath: String) {
        addPdfView(destinationPath)
        dialogProgress.dismiss()
    }

    override fun onFailure(e: Exception?) {
        dialogProgress.dismiss()
        dialogError.show(childFragmentManager)
    }

    override fun onProgressUpdate(progress: Int, total: Int) {
        dialogProgress.setProgress(progress * 100 / total)
    }
}