package mx.arquidiocesis.sos.utils

class Constants {
    class Status {
        companion object {
            //val PENDING = "PENDING"
            const val SENT = "SENT"

            const val IN_PROGRESS = "IN_PROGRESS"

            const val REJECTED = "REJECTED"
            const val COMPLETED = "COMPLETED"
            const val CANCELLED = "CANCELLED"
        }
    }

    class SubStatus {
        companion object {
            const val PENDING_CONFIRMATION = "PENDING_CONFIRMATION"
            const val ACCEPTED = "ACCEPTED"
            const val CALL_WAITING = "CALL_WAITING"
            const val CALL_FINISHED = "CALL_FINISHED"
            const val LOOKING_FOR_ASSISTANCE = "LOOKING_FOR_ASSISTANCE"
            const val HELP_ON_THE_WAY = "HELP_ON_THE_WAY"
            const val SUCCESSFULLY = "SUCCESSFULLY"
            const val UNSUCCESSFULLY = "UNSUCCESSFULLY"
            const val CANCELLED = "CANCELLED"


        }
    }
}