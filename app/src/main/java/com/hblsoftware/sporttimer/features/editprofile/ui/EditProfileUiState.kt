package com.hblsoftware.sporttimer.features.editprofile.ui

data class EditProfileUiState(
    val profileId: String? = null,
    val name: String = "",
    val preparationSeconds: Int = 10,
    val workSeconds: Int = 20,
    val restSeconds: Int = 10,
    val rounds: Int = 8,
    val saveRequested: Boolean = false,
    val errorMessage: String? = null
) {
    val totalSeconds: Int
        get() = preparationSeconds + rounds * (workSeconds + restSeconds)
}
