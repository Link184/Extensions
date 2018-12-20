package com.link184.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView

/**
 * A extension to easily load images from url directly to [ImageView] with [GlideApp]
 * @param url a url with image
 * @param block here glide can be configured
 */
inline fun ImageView.loadUrl(url: String, block: GlideRequest<Drawable>.() -> GlideRequest<Drawable> = { this }) {
    block(GlideApp.with(this).load(url)).into(this)
}

/**
 * [ImageView] and url will be grouped consecutively. Take care to pass all urls and ImageViews
 * in the same order.
 * @throws IllegalStateException when image view array size is different from url array size
 */
fun Array<ImageView>.loadUrl(vararg url: String, block: GlideRequest<Drawable>.() -> GlideRequest<Drawable> = { this }) {
    if (size != url.size) {
        throw IllegalStateException("Image view and image url count do not correspond.")
    }
    forEachIndexed { index, it ->
        it.loadUrl(url[index], block)
    }
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
infix fun <V : View> V.onClick(block: (V) -> Unit) = setOnClickListener { block(it as V) }

infix fun <V : View> Array<out V>.onClick(block: (V) -> Unit) = forEach { it onClick (block) }

/**
 * Show simple [AlertDialog]
 * @param title string resource id for dialog title
 * @param message string resource id for dialog message
 * @return an instance of shown [AlertDialog]
 */
fun Context.alert(@StringRes title: Int, @StringRes message: Int, positiveAction: (() -> Unit?)? = null): AlertDialog {
    return alert(getString(title), getString(message), positiveAction)
}

fun Context.alert(title: String, message: String, positiveAction: (() -> Unit?)? = null): AlertDialog {
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
