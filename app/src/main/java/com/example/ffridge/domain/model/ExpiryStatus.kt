package com.example.ffridge.domain.model

sealed class ExpiryStatus {
    object NoExpiry : ExpiryStatus()
    object ExpiringToday : ExpiryStatus()
    data class ExpiringSoon(val daysLeft: Int) : ExpiryStatus()
    data class ExpiringThisWeek(val daysLeft: Int) : ExpiryStatus()
    data class Fresh(val daysLeft: Int) : ExpiryStatus()
    data class Expired(val daysAgo: Int) : ExpiryStatus()
}
