package mx.upax.formacion.ui

import android.os.Handler
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import com.upax.formacion.R
import com.upax.formacion.databinding.FragmentOpenDataBinding
import es.voghdev.pdfviewpager.library.PDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import es.voghdev.pdfviewpager.library.remote.DownloadFileUrlConnectionImpl
import es.voghdev.pdfviewpager.library.util.FileUtil
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.customui.alert.ProgressDialog
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import java.io.File
import java.util.*


class OpenDataFragment :
    EAMXBaseFragment<EAMXBaseFragment.NavigationActivity, FragmentOpenDataBinding>(R.layout.fragment_open_data),
    DownloadFile.Listener {

    private var adapter: PDFPagerAdapter? = null
    private val dialogError by lazy {
        UtilAlert
            .Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(getString(R.string.title_dialog_error_load_file))
            .setListener {
                activity?.onBackPressed()
            }
            .setIsCancel(false)
            .build()
    }

    private val dialogProgress by lazy {
        ProgressDialog()
            .setOnCancel {
                it.dismiss()
                activity?.onBackPressed()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(callBack: EAMXHome): OpenDataFragment {
            val fragment = OpenDataFragment()
            fragment.callBack = callBack
            return fragment
        }
    }


    override fun initView() {
        callBack.showToolbar(true, AppMyConstants.formacion)
        arguments?.getString(WEB)?.let(::loadWeb)
        arguments?.getString(PDF)?.let(::loadPDf)
    }

    private fun loadPDf(pdfUrl: String) {
        val file = File(context?.cacheDir, FileUtil.extractFileNameFromURL(pdfUrl))

        if (file.exists()) {
            if(file.lastModified() != 0L){
                val filesModifiedDate = Calendar.getInstance()
                filesModifiedDate.timeInMillis = file.lastModified()

                val currentTime = Calendar.getInstance()
                currentTime.add(Calendar.WEEK_OF_MONTH, -1)
                //si el archivo lleva guardado menos de una semana lo muestra si no lo descarga
                if(filesModifiedDate >= currentTime){
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

    override fun onDestroy() {
        super.onDestroy()
        adapter?.close()
    }

    private fun loadWeb(url: String) {
        val webView = WebView(vBind.lRoot.context).apply {
            settings.apply {
                javaScriptEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                loadWithOverviewMode = true;
            }
            loadUrl(url)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    hideLoader()
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    hideLoader()
                }
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    hideLoader()
                }
            }
        }
        addView(webView)
    }

    private fun addView(view: View){
        vBind.lRoot.removeAllViews()
        vBind.lRoot.addView(view,  LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ))

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