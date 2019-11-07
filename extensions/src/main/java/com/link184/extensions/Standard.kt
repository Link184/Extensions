@file:JvmName("Standard")

package com.link184.extensions

import android.util.Base64
import java.io.File
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.full.declaredMemberFunctions
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

fun Date.toCalendar(): Calendar = Calendar.getInstance().apply {
    time = this@toCalendar
}

inline fun <reified T : Any> T.getPrivateMember(memberName: String) =
    T::class.declaredMemberProperties
        .firstOrNull { it.name == memberName }
        ?.let {
            it.isAccessible = true
            it.get(this@getPrivateMember)
        }

fun Any.setPrivateMember(fieldName: String, newValue: Any?) =
    this::class.java.getDeclaredField(fieldName)
        .apply {
            isAccessible = true
            val modifiersField = this::class.java.getDeclaredField("modifiers")
            modifiersField.isAccessible = true
            modifiersField.setInt(this, this.modifiers and Modifier.FINAL.inv())
            set(this@setPrivateMember, newValue)
        }


inline fun <reified T : Any> T.invokePrivateMethod(methodName: String, vararg arguments: Any?) =
    this::class.declaredMemberFunctions
        .firstOrNull { it.name == methodName }
        ?.let {
            it.isAccessible = true
            it.call(this, *arguments)
        }
