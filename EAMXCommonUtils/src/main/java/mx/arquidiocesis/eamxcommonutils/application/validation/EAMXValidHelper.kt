package mx.arquidiocesis.eamxcommonutils.application.validation

import android.annotation.SuppressLint
import android.util.Log
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import mx.arquidiocesis.eamxcommonutils.util.isValidEmail
import java.text.SimpleDateFormat
import java.util.*

class EAMXValidHelper<R>(private val modelRequest: R) {

    @SuppressLint("SimpleDateFormat")
    fun helperValidation(validation: ArrayList<EAMXValidationModel>): EAMXRequestWithValidation<R> {

        var errorMessage = ""
        var statusResult = EAMXStatusValidation.CORRECT
        validation.forEach {
            when (it.element) {
                is TextInputEditText -> {
                    when (it.typeValidation) {
                        EAMXTypeValidation.AT_LEAST -> {
                            it.valueOfValidation as Int
                            if (it.element.text.toString().trim().length < it.valueOfValidation) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.NOT_EMPTY -> {
                            it.valueOfValidation as Boolean
                            if (it.element.text.toString().trim().isEmpty()) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.EQUALS_FIELD -> {
                            it.valueOfValidation as String
                            if (it.element.text.toString().trim() == it.valueOfValidation.trim()) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.MAX_VALUE -> {
                            it.valueOfValidation as Int
                            if (it.element.text.toString().trim().length > it.valueOfValidation) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.MIN_VALUE -> {
                            it.valueOfValidation as Int
                            if (it.element.text.toString().trim().length < it.valueOfValidation) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.EQUALS -> {
                            it.valueOfValidation as TextInputEditText
                            if (it.element.text.toString()
                                    .trim() != it.valueOfValidation.text.toString().trim()
                            ) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.EMAIL_VALID -> {
                            it.valueOfValidation as Boolean

                            if (!it.element.text.toString().trim().isValidEmail()) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                    }
                }
                is EditText -> {
                    when (it.typeValidation) {
                        EAMXTypeValidation.AT_LEAST -> {
                            it.valueOfValidation as Int
                            if (it.element.text.toString().trim().length < it.valueOfValidation) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.NOT_EMPTY -> {
                            it.valueOfValidation as Boolean
                            if (it.element.text.toString().trim().isEmpty()) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.EQUALS_FIELD -> {
                            it.valueOfValidation as String
                            if (it.element.text.toString().trim() == it.valueOfValidation.trim()) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.MAX_VALUE -> {
                            it.valueOfValidation as Int
                            if (it.element.text.toString().trim().length > it.valueOfValidation) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.MIN_VALUE -> {
                            it.valueOfValidation as Int
                            if (it.element.text.toString().trim().length < it.valueOfValidation) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.EQUALS -> {
                            it.valueOfValidation as TextInputEditText
                            if (it.element.text.toString()
                                    .trim() != it.valueOfValidation.text.toString().trim()
                            ) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                        EAMXTypeValidation.EMAIL_VALID -> {
                            it.valueOfValidation as Boolean
                            if (!it.element.text.toString().trim().isValidEmail()) {
                                errorMessage += if (errorMessage.isNotEmpty()) {
                                    "\n${it.errorMessage}"
                                } else {
                                    it.errorMessage
                                }
                                statusResult = EAMXStatusValidation.INCORRECT
                            }
                        }
                    }
                }
            }
        }
        return EAMXRequestWithValidation(modelRequest, errorMessage, statusResult)
    }
}