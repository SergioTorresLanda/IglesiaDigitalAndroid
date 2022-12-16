package mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.imageslider.SliderAdapter
import com.example.imageslider.SliderItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.DialogFragmentBinding
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePromisesCreateBinding
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePromisesFragmentsBinding
import mx.arquidiocesis.eamxprofilemodule.ui.promises.EAMXProfilePromisesFragment
import mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.modelo.Model

@SuppressLint("SetTextI18n")
fun initListener(
    mBinding: EamxProfilePromisesCreateBinding,
    requireActivity: FragmentActivity?,
    adapter: RecyclerAdapter
) {


    var promise: String
    lateinit var spinnerP: String
    lateinit var spinnerT: String




    mBinding.apply {
        // Inflate the layout for this fragment
        // val di = requireActivity?.layoutInflater?.inflate(R.layout.eamx_profile_promises_create, null, false)
        //val view = mBinding.root

        val sliderItems: MutableList<SliderItem> = ArrayList()
        sliderItems.add(SliderItem(R.drawable.saint_001))
        sliderItems.add(SliderItem(R.drawable.saint_002))
        sliderItems.add(SliderItem(R.drawable.saint_003))
        sliderItems.add(SliderItem(R.drawable.saint_004))
        sliderItems.add(SliderItem(R.drawable.saint_005))
        sliderItems.add(SliderItem(R.drawable.saint_006))

        viewpager.adapter = SliderAdapter(sliderItems, viewpager)

        viewpager.clipToPadding = false
        viewpager.clipChildren = false
        viewpager.offscreenPageLimit = 3
        viewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.75f + r * 0.25f
        }
        viewpager.setPageTransformer(compositePageTransformer)





        
        ArrayAdapter.createFromResource(
            root.context,
            R.array.userprofilemarriedspinner,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val adaptador = ArrayAdapter(
                root.context,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(
                    requireActivity?.getString(R.string.txt_guadalupe),
                    requireActivity?.getString(R.string.txt_all_saint),
                    requireActivity?.getString(R.string.txt_god)
                )

            )
            userProfileMarriedSpinner.adapter = adaptador
            userProfileMarriedSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        spinnerP = userProfileMarriedSpinner.selectedItem.toString()
                    }
                }
        }

        ArrayAdapter.createFromResource(
            root.context,
            R.array.spinnerTimeList,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val adaptador = ArrayAdapter(
                root.context,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(
                    requireActivity?.getString(R.string.txt_day_one),
                    requireActivity?.getString(R.string.txt_day_three),
                    requireActivity?.getString(R.string.txt_day_five),
                    requireActivity?.getString(R.string.txt_day_fifteen),
                    requireActivity?.getString(R.string.txt_month_one)
                )

            )
            spinnerTimeList.adapter = adaptador
            spinnerTimeList.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        spinnerT = spinnerTimeList.selectedItem.toString()

                    }
                }
        }


        btnCreatePromise.setOnClickListener {

            val list = mutableListOf<Model>().apply {
                add(Model("hola"))
            }

            if (etPromise.length() == 0)
                Toast.makeText(requireActivity, "Favor de ingresar una promesa", Toast.LENGTH_SHORT)
                    .show()
            else {
                promise = etPromise.text.toString()

                val dialogView =
                    requireActivity?.layoutInflater?.inflate(R.layout.dialog_fragment, null, false)
                val dialog = MaterialAlertDialogBuilder(
                    mBinding.root.context,
                    R.style.AlertDialogTheme
                ).setView(dialogView).show()
                val bindingDialog = DialogFragmentBinding.bind(dialogView!!)
                bindingDialog.apply {

                    img.setBackgroundResource(R.drawable.saint_003)
                    txtPromise.text = "Prometiste $spinnerP $promise por $spinnerT"


                    val recy = requireActivity?.layoutInflater?.inflate(
                        R.layout.eamx_profile_promises_fragments,
                        null,
                        false
                    )
                    val bindinR = EamxProfilePromisesFragmentsBinding.bind(recy!!)

                    bindinR.apply {
                        amen.setOnClickListener {

                            list.add(Model("$promise"))
                            recycler.layoutManager?.scrollToPosition(0)
                            adapter.notifyDataSetChanged()

                            dialog.dismiss()
                            Toast.makeText(requireActivity, "" + list, Toast.LENGTH_SHORT).show()

                            val transaction =
                                requireActivity.supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.contentFragmentProfile, EAMXProfilePromisesFragment())
                            transaction.disallowAddToBackStack()
                            transaction.commit()
                        }


                        recycler.adapter = adapter
                        adapter.submitList(list)


                    }

                }
            }

        }


    }
}

fun setupRecyclerView(recycler: RecyclerView, context: Context) {
    recycler.itemAnimator = DefaultItemAnimator()
    recycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
}