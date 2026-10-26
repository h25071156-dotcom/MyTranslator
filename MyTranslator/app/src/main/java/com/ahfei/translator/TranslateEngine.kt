package com.ahfei.translator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TranslateEngine {
    suspend fun translateToTraditionalChinese(text: String): String = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext ""
        try {
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=zh-TW&dt=t&q=$encodedText"
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            
            val responseStream = connection.inputStream.bufferedReader()
            val responseString = responseStream.readText()
            responseStream.close()

            if (responseString.startsWith("[")) {
                val start = responseString.indexOf("\"") + 1
                val end = responseString.indexOf("\"", start)
                if (start in 1..end) {
                    return@withContext responseString.substring(start, end)
                }
            }
            return@withContext text
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext text
        }
    }
}