package com.example.visualword

import android.content.Context


class OpenAiText2ImageTest(
    applicationContext: Context
) {

    private fun postToOpenAI(
        apiKey: String,
        request: OpenAiImageRequest,
        onSuccess: (OpenAiImageResponse) -> Unit,
        onError: (String) -> Unit
    ) {
    }

    fun query(instruction: String, adapter: T2IViewAdapter?) {
//        val filePath = "/sdcard/Download/test_bottle.jpg"
//        val imageSource = File(filePath)
        val uri = R.drawable.test_bottle
//        if (imageSource.exists() && imageSource.canRead())
//        {
//            val uri = Uri.fromFile(imageSource)
        val newItem = TextImageDataItem(instruction, imageSource=uri)
        adapter?.updateItem(0, newItem)
//        }
    }
}
