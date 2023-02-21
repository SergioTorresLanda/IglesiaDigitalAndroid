package mx.arquidiocesis.eamxcommonutils.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import mx.arquidiocesis.eamxcommonutils.R

fun ImageView.loadByUrl(
    url: String,
    cleanCache : Boolean = true,
    isProfile : Boolean = false
) {
    when(isProfile){
        true -> {
            Glide.with(this)
                .load(url)
                .fitCenter()
                .circleCrop()
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .transition(withCrossFade())
                .skipMemoryCache(cleanCache)
                .diskCacheStrategy(if(cleanCache) DiskCacheStrategy.RESOURCE else DiskCacheStrategy.NONE)
                .into(this)
        }
        false -> {
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_place_holder_by_pictures_upload)
                .error(R.drawable.default_image)
                .transition(withCrossFade())
                .skipMemoryCache(cleanCache)
                .diskCacheStrategy(if(cleanCache) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)
                .into(this)
        }
    }
}

fun ImageView.loadByUrlSample(
    context: Context,
    url: String,
) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.ic_place_holder_by_pictures_upload)
        .error(R.drawable.default_image)
        .fitCenter()
        .circleCrop()
        .into(this)
}

fun ImageView.loadByUrlSampleSquare(
    context: Context,
    url: String,
) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.ic_place_holder_by_pictures_upload)
        .error(R.drawable.default_image)
        .fitCenter()
        .into(this)
}

fun ImageView.loadIntDrawable(resource : Int){
    Glide.with(this)
        .load(resource)
        .placeholder(R.drawable.ic_place_holder_by_pictures_upload)
        .error(R.drawable.default_image)
        .into(this)
}

fun ImageView.loadByUrlIntDrawableerror(url: String,resource : Int){
    Glide.with(this)
        .load(url)
        .placeholder(resource)
        .error(resource)
        .transition(withCrossFade())
        .skipMemoryCache(true)
        .into(this)
}