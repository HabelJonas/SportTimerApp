package com.hblsoftware.sporttimer.features.editprofile.ui

sealed interface EditProfileAction {
    data class NameChanged(val value: String) : EditProfileAction
    data object PreparationMinus : EditProfileAction
    data object PreparationPlus : EditProfileAction
    data object WorkMinus : EditProfileAction
    data object WorkPlus : EditProfileAction
    data object RestMinus : EditProfileAction
    data object RestPlus : EditProfileAction
    data object RoundsMinus : EditProfileAction
    data object RoundsPlus : EditProfileAction
    data object Reset : EditProfileAction
    data object Save : EditProfileAction
    data object SaveHandled : EditProfileAction
    data object ErrorHandled : EditProfileAction
}