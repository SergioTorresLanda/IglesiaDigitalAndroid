package mx.arquidiocesis.eamxcommonutils.util

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private fun hex2Binary(key: String): ByteArray {
    val binary = ByteArray(key.length / 2)
    for (i in binary.indices) {
        binary[i] = Integer.parseInt(key.substring(2 * i, 2 * i + 2), 16).toByte()
    }
    return binary
}

fun String.encryptData(): String {
    try {
        val randomIv = getRandomString().toByteArray()
        return if (!isNullOrEmpty()) {
            val ivSpec: AlgorithmParameterSpec = IvParameterSpec(randomIv)
            val keySpec =
                SecretKeySpec(ConstansApp.kwv().toByteArray(Charsets.UTF_8), "AES")
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding").also {
                it.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            }
            val bytes = randomIv+cipher.doFinal(toByteArray())
           Base64.encodeToString(bytes, Base64.DEFAULT)
        } else {
            ""
        }
    } catch (e: Exception) {
        //Log.i("tag", "error: " + e)
        return ""
    }
}

fun String.decryptData(): String {
    try {
        val bytes = Base64.decode(this,Base64.DEFAULT)
        val randomIv = bytes.copyOfRange(0,16)
        val data = bytes.copyOfRange(16,bytes.size)
        return if (!isNullOrEmpty()) {
            val ivSpec: AlgorithmParameterSpec = IvParameterSpec(randomIv)
            val keySpec =
                SecretKeySpec(ConstansApp.kwv().toByteArray(Charsets.UTF_8), "AES")
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding").also {
                it.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            }
            String(cipher.doFinal(data),Charsets.UTF_8)
        } else {
            ""
        }
    } catch (e: Exception) {
        //Log.i("tag", "error: " + e)
        return ""
    }
}

private fun getRandomString(digits: Int = 15): String {
    var number = ""
    for (i in 0..digits) {
        number += if ((0..1).random() == 0)
            ('A'..'Z').random()
        else (0..9).random()
    }
    return number
}