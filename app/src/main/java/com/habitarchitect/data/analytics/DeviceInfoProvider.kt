package com.habitarchitect.data.analytics

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import com.habitarchitect.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides device information for analytics context.
 */
@Singleton
class DeviceInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Get the app version name.
     */
    fun getAppVersion(): String = BuildConfig.VERSION_NAME

    /**
     * Get the Android OS version.
     */
    fun getOsVersion(): String = Build.VERSION.RELEASE

    /**
     * Get the device type (phone or tablet).
     */
    fun getDeviceType(): String {
        val screenLayout = context.resources.configuration.screenLayout
        val isTablet = (screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
        return if (isTablet) "tablet" else "phone"
    }

    /**
     * Get the screen density bucket.
     */
    fun getScreenDensityBucket(): String {
        return when (context.resources.displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "ldpi"
            DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            DisplayMetrics.DENSITY_HIGH -> "hdpi"
            DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
            else -> "unknown"
        }
    }

    /**
     * Get device manufacturer.
     */
    fun getManufacturer(): String = Build.MANUFACTURER

    /**
     * Get device model.
     */
    fun getModel(): String = Build.MODEL
}
