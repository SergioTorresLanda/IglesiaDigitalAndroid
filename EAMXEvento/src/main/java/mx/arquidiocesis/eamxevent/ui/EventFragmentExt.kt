package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_event_pantries.*
import kotlinx.android.synthetic.main.item_event_detail.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment

fun EventFragment.setupInit(guest:Boolean){
    val profile = eamxcu_preferences.getData(
        EAMXEnumUser.USER_PROFILE.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String
    if (guest) {
        tvNewEvent.visibility = View.GONE
    }
    when (profile) {
        EAMXProfile.Devoted.rol, EAMXProfile.Priest.rol
        -> {
            tvNewEvent.visibility = View.GONE
        }
    }
}

fun EventPantriesFragment.setupInit(guest:Boolean){
    val profile = eamxcu_preferences.getData(
        EAMXEnumUser.USER_PROFILE.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String
    if (guest) {
        tvNewDespensa.visibility = View.GONE
    }
    when (profile) {
        EAMXProfile.Devoted.rol, EAMXProfile.Priest.rol
        -> {
            tvNewDespensa.visibility = View.GONE
        }
    }
}

fun EventFragment.setupRecyclerView() {
    rvEvents.adapter = adapter
    rvEvents.layoutManager = LinearLayoutManager(context)
    rvEvents.itemAnimator = DefaultItemAnimator()
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }
}

fun EventPantriesFragment.setupRecyclerView() {
    rvDespensas.adapter = adapterPantry
    rvDespensas.layoutManager = LinearLayoutManager(context)
    rvDespensas.itemAnimator = DefaultItemAnimator()
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }
}