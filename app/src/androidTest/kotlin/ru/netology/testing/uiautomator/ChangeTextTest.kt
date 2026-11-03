package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"
const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"

    @Before
    fun beforeEachTest() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)
        device.findObject(
            UiSelector().resourceId("android:id/title").instance(0)
        ).click()
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)
        Thread.sleep(1000) // Пауза

        device.findObject(By.res(packageName, "userInput")).text = textToSet
        Thread.sleep(500)

        device.findObject(By.res(packageName, "buttonChange")).click()
        Thread.sleep(500)

        val result = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(textToSet, result)
    }

    @Test
    fun testEmptyText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)
        Thread.sleep(1000)

        // 1. Запоминаем текущий текст (до ввода)
        val textBefore = device.findObject(By.res(packageName, "textToBeChanged")).text

        // 2. Устанавливаем пустую строку (пробелы)
        device.findObject(By.res(packageName, "userInput")).text = "   "
        Thread.sleep(500)

        // 3. Нажимаем на кнопку изменения
        device.findObject(By.res(packageName, "buttonChange")).click()
        Thread.sleep(500)

        // 4. Проверяем, что текст НЕ изменился
        val textAfter = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(textBefore, textAfter)
    }

    @Test
    fun testOpenNewActivity() {
        val packageName = MODEL_PACKAGE
        val textToSet = "Netology"
        waitForPackage(packageName)
        Thread.sleep(1000)

        // 1. Вводим текст
        device.findObject(By.res(packageName, "userInput")).text = textToSet
        Thread.sleep(500)

        // 2. Нажимаем на кнопку (используем найденный ID buttonActivity)
        device.findObject(By.res(packageName, "buttonActivity")).click()
        Thread.sleep(1000)

        // 3. Ждем появления второго экрана (ищем текст, который ввели)
        device.wait(Until.hasObject(By.text(textToSet)), TIMEOUT)

        // 4. Проверяем, что текст на экране совпадает с введенным
        val result = device.findObject(By.text(textToSet)).text
        assertEquals(textToSet, result)
    }
}


