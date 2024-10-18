package com.example.androidapputility.utility

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AESUtil {

    companion object {

        private const val ALGORITHM = "AES"
        private const val KEY = "your-secret-key!" // 長度必須是 16、24 或 32 字節

        fun encrypt(input: String): String {
            val cipher = Cipher.getInstance(ALGORITHM)
            val keySpec = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encrypted = cipher.doFinal(input.toByteArray())
            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        }

        fun decrypt(encrypted: String): String {
            val cipher = Cipher.getInstance(ALGORITHM)
            val keySpec = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decoded = Base64.decode(encrypted, Base64.DEFAULT)
            val decrypted = cipher.doFinal(decoded)
            return String(decrypted)
        }
    }
}