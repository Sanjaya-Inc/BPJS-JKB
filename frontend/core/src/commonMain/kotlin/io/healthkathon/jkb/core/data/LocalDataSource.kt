package io.healthkathon.jkb.core.data

import com.russhwolf.settings.Settings

expect class LocalDataSource : Settings {
    override fun clear()
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean
    override fun getBooleanOrNull(key: String): Boolean?
    override fun getDouble(key: String, defaultValue: Double): Double
    override fun getDoubleOrNull(key: String): Double?
    override fun getFloat(key: String, defaultValue: Float): Float
    override fun getFloatOrNull(key: String): Float?
    override fun getInt(key: String, defaultValue: Int): Int
    override fun getIntOrNull(key: String): Int?
    override fun getLong(key: String, defaultValue: Long): Long
    override fun getLongOrNull(key: String): Long?
    override fun getString(key: String, defaultValue: String): String
    override fun getStringOrNull(key: String): String?
    override fun hasKey(key: String): Boolean
    override fun putBoolean(key: String, value: Boolean)
    override fun putDouble(key: String, value: Double)
    override fun putFloat(key: String, value: Float)
    override fun putInt(key: String, value: Int)
    override fun putLong(key: String, value: Long)
    override fun putString(key: String, value: String)
    override fun remove(key: String)
    override val keys: Set<String>
    override val size: Int
}

expect fun createLocalDataSource(): io.healthkathon.jkb.core.data.LocalDataSource
