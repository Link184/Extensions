package com.link184.extensions

import android.util.Log

/** Developer friendly set of log extensions */
fun Any.logd(value: String,  error: Throwable? = null, tag: LoggerTAG = LoggerTAG(this::class.java.simpleName)) = Log.d(tag.tag, value, error)
fun Any.logi(value: String,  error: Throwable? = null, tag: LoggerTAG = LoggerTAG(this::class.java.simpleName)) = Log.i(tag.tag, value, error)
fun Any.logv(value: String,  error: Throwable? = null, tag: LoggerTAG = LoggerTAG(this::class.java.simpleName)) = Log.v(tag.tag, value, error)
fun Any.logw(value: String,  error: Throwable? = null, tag: LoggerTAG = LoggerTAG(this::class.java.simpleName)) = Log.w(tag.tag, value, error)
fun Any.loge(value: String,  error: Throwable? = null, tag: LoggerTAG = LoggerTAG(this::class.java.simpleName)) = Log.e(tag.tag, value, error)
fun Any.logwtf(value: String, error: Throwable? = null, tag: LoggerTAG = LoggerTAG(this::class.java.simpleName)) = Log.wtf(tag.tag, value, error)

class LoggerTAG(internal val tag: String)