package com.link184.extensions

import android.util.Base64
import java.io.File
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun File.toBase64(): String = Base64.encodeToString(readBytes(), Base64.DEFAULT)

/**
 * Reflective parse a java class and extract all fields names and values grouped in a map.
 * @return a map of key as class field name and value as class field value.
 */
inline fun <reified T : Any> T.toMap(): Map<String, Any?> {
    return T::class.java.declaredFields.associate {
        it.isAccessible = true
        it.name to it.get(this)
    }
}

fun Int.fromCents(): CharSequence? {
    return "%.2f".format(toBigDecimal().divide(BigDecimal(100.00)))
}

fun Date.toCalendar(): Calendar = Calendar.getInstance().apply {
    time = this@toCalendar
}

inline fun <reified T: Any> T.getPrivateMember(memberName: String) =
        T::class.declaredMemberProperties
                .firstOrNull { it.name == memberName }
                ?.let {
                    it.isAccessible = true
                    it.get(this@getPrivateMember)
                }
