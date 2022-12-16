package mx.arquidiocesis.eamxcommonutils.util.actioninerfaz

object EAMXActioknManager {

    fun registerInterfaceAction(listener: EAMXActionInterface) {
        listener.onBackPressedAction()
    }

}
