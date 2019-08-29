package com.link184.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputLayout

/**
 * A extension to easily load images from url directly to [ImageView] with [GlideApp]
 * @param url a url with image
 * @param block here glide can be configured
 */
inline fun ImageView.loadUrl(url: String, block: GlideRequest<Drawable>.() -> GlideRequest<Drawable> = { this }) {
    loadUri(Uri.parse(url), block)
}

/**
 * [ImageView] and url will be grouped consecutively. Take care to pass all urls and ImageViews
 * in the same order.
 * @throws IllegalStateException when image view array size is different from url array size
 */
inline fun Array<ImageView>.loadUrl(vararg url: String, block: GlideRequest<Drawable>.() -> GlideRequest<Drawable> = { this }) {
    loadUri(*url.map { Uri.parse(it) }.toTypedArray(), block = block)
}
/**
 * A extension to easily load images from url directly to [ImageView] with [GlideApp]
 * @param url a url with image
 * @param block here glide can be configured
 */
inline fun ImageView.loadUri(uri: Uri, block: GlideRequest<Drawable>.() -> GlideRequest<Drawable> = { this }) {
    block(GlideApp.with(this).load(uri)).into(this)
}

/**
 * [ImageView] and url will be grouped consecutively. Take care to pass all urls and ImageViews
 * in the same order.
 * @throws IllegalStateException when image view array size is different from url array size
 */
inline fun Array<ImageView>.loadUri(vararg uri: Uri, block: GlideRequest<Drawable>.() -> GlideRequest<Drawable> = { this }) {
    if (size != uri.size) {
        throw IllegalStateException("Image view and image url count do not correspond.")
    }
    forEachIndexed { index, it ->
        it.loadUri(uri[index], block)
    }
}

/**
 * Loads a remote image into a [Drawable]
 * @param uri image uri
 * @param onSuccess drawable ready callback
 * @param onFailure on failure callback
 */
fun Context.loadBitmap(uri: Uri, width: Int, height: Int, onSuccess: (Bitmap) -> Unit, onFailure: (Drawable?) -> Unit = {}) {
    GlideApp.with(this)
        .asBitmap()
        .load(uri)
        .into(object : CustomTarget<Bitmap>(width, height) {
            override fun onLoadCleared(placeholder: Drawable?) {}

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                onFailure(errorDrawable)
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onSuccess(resource)
            }
        })
}
fun Fragment.loadBitmap(uri: Uri,  width: Int, height: Int, onSuccess: (Bitmap) -> Unit, onFailure: (Drawable?) -> Unit = {}) {
    context?.loadBitmap(uri,  width, height, onSuccess, onFailure)
}
fun View.loadBitmap(uri: Uri,  width: Int, height: Int, onSuccess: (Bitmap) -> Unit, onFailure: (Drawable?) -> Unit = {}) {
    context?.loadBitmap(uri,  width, height, onSuccess, onFailure)
}

/**
 * Helper extension to avoid explicit text extraction from [TextInputLayout]
 */
fun TextInputLayout.text(): String = editText?.text.toString()

fun TextInputLayout.text(text: String) = editText?.setText(text)

fun TextInputLayout.text(@StringRes textResId: Int) = editText?.setText(textResId)

/**
 * Hack to hold concrete view type(not supertype) directly in listener.
 */
@Suppress("UNCHECKED_CAST")
infix fun <V : View> V.onClick(block: V.() -> Unit) = setOnClickListener { block(it as V) }

inline infix fun <V : View> Array<out V>.onClick(block: V.() -> Unit) = forEach { it onClick (block) }

/**
 * Show simple [AlertDialog]
 * @param title string resource id for dialog title
 * @param message string resource id for dialog message
 * @return an instance of shown [AlertDialog]
 */
fun Context.alert(@StringRes title: Int, @StringRes message: Int, positiveAction: (() -> Unit)? = null): AlertDialog {
    return alert(getString(title), getString(message), positiveAction)
}

fun Context.alert(title: String, message: String, positiveAction: (() -> Unit)? = null): AlertDialog {
    return AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ -> positiveAction?.invoke() }
            .show()
}

/**
 * Useful to map views into sharable elements
 * @return an array of pairs of [View] and transaction name. A common form for android transition
 * framework.
 */
fun Array<out View>.toSharableElements(): Array<Pair<View, String?>> =
        map { Pair(it, ViewCompat.getTransitionName(it)) }.toTypedArray()

/**
 * @param block function is triggered when every text change
 */
fun EditText.onTextChange(block: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { block(s.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}
