package mx.arquidiocesis.eamxcommonutils.application.validation

import com.google.android.material.textfield.TextInputLayout

data class EAMXValidationModel(val element: Any,
                               val typeValidation: EAMXTypeValidation,
                               val valueOfValidation: Any,
                               val errorMessage: String)