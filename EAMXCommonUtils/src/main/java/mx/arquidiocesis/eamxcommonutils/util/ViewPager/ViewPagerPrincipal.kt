package mx.arquidiocesis.eamxcommonutils.util.ViewPager

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import mx.arquidiocesis.eamxcommonutils.model.ViewPagerModel
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.databinding.OnBoardingBinding
import mx.arquidiocesis.eamxcommonutils.util.Adapter.ViewPagerAdapter

class ViewPagerPrincipal(
    val list: List<ViewPagerModel>,
    val btnSheet: Boolean = true,
    val listenerClose: (Any) -> Unit
) :
    DialogFragment() {
    lateinit var binding: OnBoardingBinding
    var isFinal = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OnBoardingBinding.inflate(layoutInflater, container, false)
        binding.apply {
            if (!btnSheet) {
                clBottomSheat.backgroundTintList =
                    requireContext().getResources().getColorStateList(R.color.color_transparente)
                clBottomSheat.elevation = 0f
                tlOnBording.visibility = View.GONE
                cvOnBording.visibility = View.GONE
                btnSiguiente.visibility = View.VISIBLE
            }
            if (!list.isNullOrEmpty()) {
                var adapterVP = ViewPagerAdapter(this@ViewPagerPrincipal, list, {
                    listenerClose(true)
                }) {
                    if (isFinal) {
                        listenerClose(true)
                    } else {
                        vpOnBording.currentItem = vpOnBording.currentItem + 1
                    }
                }
                btnSiguiente.setOnClickListener {
                    if (isFinal) {
                        listenerClose(true)
                    } else {
                        vpOnBording.currentItem = vpOnBording.currentItem + 1
                    }
                }
                vpOnBording.apply {
                    adapter = adapterVP
                }
                if (list.size > 1) {
                    if (btnSheet) {
                        tlOnBording.count = list.size
                    }
                    vpOnBording.registerOnPageChangeCallback(object :
                        ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            if (btnSheet) {
                                tlOnBording.setSelected(position)
                            }
                            if (list[position].imagenes != null) {
                                ivDevotee.setImageBitmap(list[position].imagenes)
                            }
                            if (position == list.size - 1) {
                                isFinal = true
                                btnSiguiente.text = "Finalizar"
                            } else {
                                btnSiguiente.text = "Siguiente"
                            }
                        }

                        override fun onPageScrollStateChanged(state: Int) {
                            super.onPageScrollStateChanged(state)
                        }

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        }
                    })

                } else {
                    this.tlOnBording.visibility=View.GONE
                    this.btnSiguiente.text = "Finalizar"
                    isFinal = true
                    if (list.first().imagenes != null) {
                        ivDevotee.setImageBitmap(list.first().imagenes)
                    }
                }

            }
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

}