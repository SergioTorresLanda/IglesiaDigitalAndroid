package mx.upax.eamxservices

import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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
    fun testChangeDate(){
        val patternLetterWithInitialMonths = Pattern.compile( "[efmajsond]")
        val dateReceivedString2 = "2021-12-02 10:29:20"
        val dateReceivedString = "2021-08-13 15:30:53"
        val formatReceived = "yyyy-MM-dd"
        val formatOut = "dd MMMM yyyy"

        val simpleFormat = SimpleDateFormat(formatReceived, Locale("es", "ES"))
        val dateReceived: Date? = simpleFormat.parse(dateReceivedString)
        simpleFormat.applyPattern(formatOut)
        val dateOk = StringBuilder().append(dateReceived?.let{ simpleFormat.format(it) } ?: kotlin.run {""})

        if(dateOk.isNotEmpty()){
            //This comparation is when day has one digit
           when(patternLetterWithInitialMonths.matcher(dateOk.substring(3,4).toLowerCase()).find()) {
               true -> dateOk.setRange(3,4, dateOk.substring(3,4).toUpperCase())
               //This comparation is when day has two digit
               false -> when (patternLetterWithInitialMonths.matcher(dateOk.toString().substring(2,3).toLowerCase()).find()) {
                   true -> dateOk.setRange(2,3                                             , dateOk.substring(2,3).toUpperCase())
                   false -> print("Not funding initial data")
               }
           }
        }

        Assert.assertNotNull(dateOk)
    }

    @Test
    fun testDateInFormatDayNameDDMonthName(){
        val formatOut = "EEEE dd MMMM"
        val formatCalendar = "yyyy-MM-dd"
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DATE, 19)
        calendar.set(Calendar.MONTH, 7)
        calendar.set(Calendar.YEAR, 2021)

        val simpleFormat = SimpleDateFormat(formatCalendar, Locale("es", "MX"))
        simpleFormat.applyPattern(formatOut)
        val dateArray = simpleFormat.format(calendar.time).split(" ").toMutableList()//StringBuilder().append(simpleFormat.format(calendar.time))
        dateArray[0] = dateArray[0].replaceRange(0..0, dateArray[0].substring(0,1).toUpperCase())
        dateArray[2] = dateArray[2].replaceRange(0..0, dateArray[2].substring(0,1).toUpperCase())
        val dateOk = "${dateArray[0]} ${dateArray[1]} ${dateArray[2]}"
        print(dateOk)
    }

    @Test
    fun testDateInFormatDayNameDDMonthNameCurrent(){
        val formatOut = "EEEE dd MMMM"
        val formatCalendar = "yyyy-MM-dd"
        val calendar = Calendar.getInstance()

        val simpleFormat = SimpleDateFormat(formatCalendar, Locale("es", "MX"))
        simpleFormat.applyPattern(formatOut)
        val dateArray = simpleFormat.format(calendar.time).split(" ").toMutableList()//StringBuilder().append(simpleFormat.format(calendar.time))
        dateArray[0] = dateArray[0].replaceRange(0..0, dateArray[0].substring(0,1).toUpperCase())
        dateArray[2] = dateArray[2].replaceRange(0..0, dateArray[2].substring(0,1).toUpperCase())
        val dateOk = "${dateArray[0]} ${dateArray[1]} ${dateArray[2]}"
        print(dateOk)
    }
}

