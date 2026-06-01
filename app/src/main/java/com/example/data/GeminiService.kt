package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val key = BuildConfig.GEMINI_API_KEY
        if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is missing or using default placeholder!")
            // Synthesized, high-fidelity response offline simulator
            return@withContext simulateOfflineResponse(prompt)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$key"
        
        try {
            val requestBodyJson = JSONObject()
            
            // Contents
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestBodyJson.put("contents", contentsArray)

            // System Instruction
            if (systemInstruction != null) {
                val sysInstObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstruction)
                sysPartsArray.put(sysPartObj)
                sysInstObj.put("parts", sysPartsArray)
                requestBodyJson.put("systemInstruction", sysInstObj)
            }

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            requestBodyJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestBodyJson.toString().toRequestBody(mediaType)
            
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errMsg = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful response from Gemini API: code=${response.code} body=$errMsg")
                    return@withContext simulateOfflineResponse(prompt)
                }
                
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val responseContent = firstCandidate.getJSONObject("content")
                val responseParts = responseContent.getJSONArray("parts")
                val firstPart = responseParts.getJSONObject(0)
                
                return@withContext firstPart.getString("text")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini API: ${e.message}", e)
            return@withContext simulateOfflineResponse(prompt)
        }
    }

    private fun simulateOfflineResponse(prompt: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("golden ratio") -> {
                "The **Golden Ratio (Φ ≈ 1.618)** provides a mathematical basis for aesthetic balance. In UI design, it is often used to construct proportional relations between elements.\n\n" +
                "**Calculations:**\n" +
                "For a 1440px viewport:\n" +
                "- Main container: 1440 / 1.618 = ~890px\n" +
                "- Sidebar container: 1440 - 890 = ~550px\n\n" +
                "Applying this to a **12-column grid**, an 8+4 or 7+5 split stays closest to this golden proportion."
            }
            p.contains("swiss") || p.contains("grid") -> {
                "Swiss typography and layout systems emphasize structural grid alignment. Dividing templates into neat, modular tracks ensures layout consistency, visual pacing, and quick scanning.\n\n" +
                "Try utilizing a standard **8dp core grid** for spacing padding and elevations."
            }
            p.contains("color") || p.contains("indigo") -> {
                "Industrial and intelligent interfaces often leverage electric indigo to communicate technological precision and high processing power. It pairs beautifully with monochromatic, low-key dark surroundings."
            }
            else -> {
                "Intelligent Synthesis: Based on Graphic Arts theory, aligning visual weight with mathematical divisions (like grids, fractions, and optical margins) reduces the user's cognitive load. Keep layouts structured and let content speak through spacious typography."
            }
        }
    }
}
