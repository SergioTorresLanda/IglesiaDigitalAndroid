package mx.arquidiocesis.eamxcommonutils.util.socialsupport.crypto


import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object EAMXEncripAES {

//    const val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
//    const val secretKey = "NjE2QzFCNDdCRTMyRDYyNDYxMDVEQjJFMTIzNDU2Nzg=" //lave que me proporcionaron convertida a base 64 mas los 8 digitos
    private val salt = ConstansApp.chSalt() // base64 decode => AiF4sa12SAfvlhiWu
//    const val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    private val secretKey = ConstansApp.chSecretKey()
//    const val iv = "1734df9a3c5fba88d221ca" //funciona pero son 22 caracteres
//    const val iv = "1734df9a3c5fba88"
    private val iv = ConstansApp.chIv() //iv que me proporcionaron comvertido a base 64

    fun digitRandome(): Int {
        var i = 1
        var num = ""
        while ( i <= 8) {
            i++
            val n = (0..9).random()
            num += n
        }
        return  num.toInt()
    }

    fun encrypt(strToEncrypt: String): String? {
        try {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec = PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt: String): String? {
        try {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec = PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        } catch (e: Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }
}