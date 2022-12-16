package  mx.arquidiocesis.eamxcommonutils.customui.alert

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.databinding.AlertFragmentBinding



class UtilAlert private constructor(
        private var title: String?,
        private var message: String?,
        private var textButtonOk: String?,
        private var options : ArrayList<String>?,
        private var textButtonCancel: String?,
        private var titleCustom: SpannableString?,
        private var messageCustom: SpannableString?,
        private var isCancelCustom: Boolean?,
        private var typeAlert: String?,
        private val listener: (Any) -> Unit = {}
) : DialogFragment() {

    companion object{
        const val ACTION_ACCEPT = "ACCEPT"
        const val ACTION_CANCEL = "CANCEL"
        const val ACTION_CLOSE = "CLOSE"
        const val TYPE_ALERT_SIMPLE = "SIMPLE"
        const val TYPE_ALERT_MULTI_SELECTION = "MULTISELECTION"
    }

    class Builder(){
        private var title: String? = null
        private var message: String? = null
        private var titleCustom: SpannableString? = null
        private var messageCustom: SpannableString? = null
        private var textButtonOk: String? = null
        private var options: ArrayList<String>? = null
        private var textButtonCancel: String? = null
        private var isCancel: Boolean? = null
        private var typeAlert: String? = null
        private var listener: (Any) -> Unit = {}

        fun setTypeAlert(typeAlert: String) : Builder{
            this.typeAlert = typeAlert
            return this
        }

        fun setOptions(options : ArrayList<String>) : Builder{
            this.options = options
            return this
        }

        fun setTitle(title: String) : Builder{
            this.title = title
            return this
        }

        fun setMessage(message: String) : Builder{
            this.message = message
            return this
        }

        fun setIsCancel(isCancel: Boolean) : Builder{
            this.isCancel = isCancel
            return this
        }

        fun setTextButtonOk(text: String) : Builder{
            this.textButtonOk =  text
            return this
        }

        fun setTextButtonCancel(text: String) : Builder{
            this.textButtonCancel = text
            return this
        }

        fun setListener(listener: (Any) -> Unit) : Builder{
            this.listener = listener
            return this
        }

        /**
         * Método para poder mostrar con estilo el texto del dialog
         */
        fun setMessageCustom(spannableString: SpannableString): Builder {
            this.messageCustom = spannableString
            return this
        }

        /**
         * Método para poder mostrar con estilo el texto del dialog
         */
        fun setTitleCustom(spannableString: SpannableString): Builder {
            this.titleCustom = spannableString
            return this
        }

        fun build() : UtilAlert {
            return UtilAlert(
                    title,
                    message,
                    textButtonOk,
                    options,
                    textButtonCancel,
                    titleCustom,
                    messageCustom,
                    isCancel,
                    typeAlert,
                    listener,
                )
        }
    }

    private lateinit var binding: AlertFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding = AlertFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(this.typeAlert){
            TYPE_ALERT_MULTI_SELECTION ->{
                buildAlertMultiSelection(binding)
            }
            TYPE_ALERT_SIMPLE -> {
                buildAlertSimple(binding)
            }
            else -> buildAlertSimple(binding)
        }
    }

    private fun buildAlertMultiSelection(binding: AlertFragmentBinding) {
        binding.apply {

            llMultiSelection.visibility = View.VISIBLE
            llAlertSimple.visibility = View.GONE

            ivClose.setOnClickListener {
                dismiss()
                listener(ACTION_CLOSE)
            }

            if(title == null && titleCustom == null) {
                tvTitleMultiSelection.visibility = View.GONE
            }else{
                title?.let {
                    tvTitleMultiSelection.text = it
                } ?: titleCustom?.let{
                    tvTitleMultiSelection.text = titleCustom
                    tvTitleMultiSelection.textSize = 14.0f
                }
            }

            options?.let{
                lvMultiSelection.apply {
                    adapter = ArrayAdapter(context, R.layout.row_multi_selection, it)
                    setOnItemClickListener { _, _, position, _ ->
                        dismiss()
                        listener(it[position])
                    }
                }
            }
        }
    }

    fun show(manager: FragmentManager) =
        super.show(manager, this::class.simpleName)


    private fun buildAlertSimple(binding: AlertFragmentBinding){

        binding.apply {

            llAlertSimple.visibility = View.VISIBLE
            llMultiSelection.visibility = View.GONE

            if(message == null && messageCustom == null){
                tvBody.visibility = View.GONE
            }else {
                tvBody.text = message ?: messageCustom
            }

            if(title == null && titleCustom == null) {
                tvTitle.visibility = View.GONE
            }else{
                title?.let {
                    tvTitle.text = it
                } ?: titleCustom?.let{
                    tvTitle.text = titleCustom
                    tvTitle.textSize = 16.0f
                }
            }

            textButtonOk?.let {
                btnAccept.text = it
            }

            if(textButtonCancel != null) {
                btnCancel.text = textButtonCancel
                btnCancel.visibility = View.VISIBLE
            }else{
                btnCancel.visibility = View.GONE
            }

            isCancelCustom?.let{
                isCancelable = it
            }

            ivClose.setOnClickListener {
                dismiss()
                listener(ACTION_CLOSE)
            }

            btnAccept.setOnClickListener {
                dismiss()
                listener(ACTION_ACCEPT)
            }

            btnCancel.setOnClickListener {
                dismiss()
                listener(ACTION_CANCEL)
            }
        }
    }
}