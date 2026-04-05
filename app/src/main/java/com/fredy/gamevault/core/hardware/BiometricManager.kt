package com.fredy.gamevault.core.hardware

import android.os.Build
import androidx.biometric.BiometricManager as AndroidXBiometricManager
import androidx.biometric.BiometricPrompt as AndroidXBiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricManager @Inject constructor() {

    fun isBiometricAvailable(activity: FragmentActivity): BiometricAvailability {
        val biometricManager = AndroidXBiometricManager.from(activity)

        return when (
            biometricManager.canAuthenticate(
                AndroidXBiometricManager.Authenticators.BIOMETRIC_STRONG or
                    AndroidXBiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        ) {
            AndroidXBiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            AndroidXBiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NO_HARDWARE
            AndroidXBiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HW_UNAVAILABLE
            AndroidXBiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NONE_ENROLLED
            else -> BiometricAvailability.UNAVAILABLE
        }
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String = "Autenticacion Biometrica",
        description: String = "Usa tu huella digital para acceder",
        negativeButtonText: String = "Cancelar",
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit = {}
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = AndroidXBiometricPrompt(
            activity,
            executor,
            object : AndroidXBiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: AndroidXBiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    onFailed()
                }
            }
        )

        val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AndroidXBiometricManager.Authenticators.BIOMETRIC_STRONG or
                AndroidXBiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            AndroidXBiometricManager.Authenticators.BIOMETRIC_STRONG
        }

        val promptBuilder = AndroidXBiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            promptBuilder.setNegativeButtonText(negativeButtonText)
        }

        biometricPrompt.authenticate(promptBuilder.build())
    }
}

enum class BiometricAvailability {
    AVAILABLE,
    NO_HARDWARE,
    HW_UNAVAILABLE,
    NONE_ENROLLED,
    UNAVAILABLE
}