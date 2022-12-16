package mx.arquidiocesis.eamxgeneric

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun text() {
        val text = "Example local unit test, which will execute on the development machine (host). https://mail.google.com/mail/u/2/#search/mx-at?projector=1&messagePartId=0.2 Example local unit test, which will execute on the development machine (host)."
        val urlFound = text.substring(text.indexOf("http"))
        val array = urlFound.split(" ")
        val urlClean = array.firstOrNull()
        print(urlClean)
        assertNotNull(urlClean)

        val text2 = "Example local unit test, which will execute on the development machine (host)."

        val positionUrl = text.indexOf("http")

        val arrayTextNew = arrayListOf<String>()
        if(positionUrl > -1){
            val arrayTextAndUrl = text2.substring(positionUrl).split(" ").filter { text -> !text.contains("http") }
            arrayTextAndUrl.forEach { item ->
                //Se enriquese el texto
                item.plus("mas data")
            }

        }else{

        }
    }
}