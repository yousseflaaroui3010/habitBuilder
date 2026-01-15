package com.habitarchitect.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitarchitect.data.analytics.AnalyticsLocalStorage
import com.habitarchitect.data.analytics.AnalyticsTracker
import com.habitarchitect.data.analytics.ConsentManager
import com.habitarchitect.domain.analytics.ConsentTier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentSettingsViewModel @Inject constructor(
    private val consentManager: ConsentManager,
    private val analyticsLocalStorage: AnalyticsLocalStorage,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    val consentTier = consentManager.observeConsentTier()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConsentTier.ESSENTIAL
        )

    fun setConsentTier(tier: ConsentTier) {
        viewModelScope.launch {
            val oldTier = consentManager.getConsentTier()

            // If downgrading, delete events above new tier
            if (tier.ordinal < oldTier.ordinal) {
                analyticsLocalStorage.handleConsentDowngrade(tier)
            }

            consentManager.setConsentTier(tier)

            // Track consent change (this event is always ESSENTIAL tier)
            analyticsTracker.trackConsentChanged(oldTier, tier)
        }
    }
}
