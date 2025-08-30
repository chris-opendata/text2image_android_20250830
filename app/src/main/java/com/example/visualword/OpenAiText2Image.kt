package com.example.visualword

import android.content.Context
import android.util.Log
import android.util.Base64
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.android.volley.ClientError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import com.google.gson.annotations.SerializedName


//data class Message(
//    val role: String,
//    val content: String
//)
//
//data class OpenAiRequest(
//    val model: String,
//    val messages: List<Message>,
//    val temperature: Float = 0.7f
//)
//
//data class Choice(
//    val message: Message
//)
//
//data class OpenAiResponse(
//    val choices: List<Choice>
//)

data class OpenAiImageRequest(
    val prompt: String,
    val model: String = "dall-e-3",
    val n: Int = 1,
    val size: String = "256x256",
    @SerializedName("response_format")
    val responseFormat: String = "url"
)

data class ImageData(
    val url: String? = null,
    @SerializedName("b64_json")
    val b64Json: String? = null
)

data class OpenAiImageResponse(
//    val created: Long,
    val data: List<ImageData>
)

class OpenAiText2Image(
    applicationContext: Context
) {
    private var queue: RequestQueue = Volley.newRequestQueue(applicationContext)
    private var model = "dall-e-2" // "dall-e-3" or "gpt-4o-mini-2024-07-18"
    private var url = "https://api.openai.com/v1/images/generations"
    private var APIKEY = "your registered openai App Key"

    private fun postToOpenAI(
        apiKey: String,
        request: OpenAiImageRequest,
        onSuccess: (OpenAiImageResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val gson = Gson()
        val jsonRequest = gson.toJson(request)

        // Create a JsonObjectRequest
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, JSONObject(jsonRequest),
            { response ->
                val openAiImageResponse = gson.fromJson(response.toString(), OpenAiImageResponse::class.java)
                onSuccess(openAiImageResponse)
            },
            { error ->
                if (error is ClientError) {
                    val networkResponse = error.networkResponse
                    if (networkResponse != null) {
                        val statusCode = networkResponse.statusCode
                        val responseData = String(networkResponse.data)
                        Log.e("VolleyError", "ClientError: Status Code = $statusCode, Response = $responseData")
                        // Handle specific error codes (400, 401, 403, 404, etc.)
                    }
                } else {
                    // Handle other error types
                }
                onError(error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer $apiKey"
                return headers
            }
        }
        //Optional parameters
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(jsonObjectRequest)
    }

    fun query(instruction: String, adapter: T2IViewAdapter?) {
        val prompt = "Generate an image for $instruction"
        val openAiRequest = OpenAiImageRequest(
                                prompt = prompt,
                                model = model,
                                responseFormat = "b64_json"
                            )

        postToOpenAI(
            APIKEY,
            openAiRequest,
            onSuccess = { response ->
                val imageData = response.data.firstOrNull()
                if (imageData?.url != null) {
                    val newItem = TextImageDataItem(openAiRequest.prompt, imageSource=imageData.url)
                    adapter?.updateItem(0, newItem)
                }
                else if (imageData?.b64Json != null) {
                    val imageSource = decodeBase64Image(imageData.b64Json)
                    val newItem = TextImageDataItem(openAiRequest.prompt, imageSource=imageSource)
                    adapter?.updateItem(0, newItem)
                }
            },
            onError = { error ->
                Log.d("OpenAIDebug", "query openai error: $error")
            }
        )
    }

    private fun decodeBase64Image(base64String: String): Bitmap {
        try {
            val decodedBytes: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
