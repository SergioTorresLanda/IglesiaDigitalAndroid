package mx.arquidiocesis.eamxgeneric.customviews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import mx.arquidiocesis.eamxgeneric.databinding.FragmentNotificationBinding

class NotificationFragment(
    var title: String,
    var body: String,
    val listener: (Any) -> Unit
) : DialogFragment() {

    lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dialog?.getWindow()?.setLayout(1280, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTitle.text = title
            tvBody.text = body

            btnRefuse.setOnClickListener {
                listener("REFUSED")
                dialog?.dismiss()
            }

            btnSchedule.setOnClickListener {
                listener("SCHEDULE")
                dialog?.dismiss()
            }
        }
    }
}