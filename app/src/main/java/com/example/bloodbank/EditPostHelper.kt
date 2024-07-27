package com.example.bloodbank

import android.content.Context
import android.content.Intent

class EditPostHelper(private val context: Context) {

    fun showEditPostDialog(post: UserPost) {
        val intent = Intent(context, EditPostActivity::class.java).apply {
            putExtra("post", post)
        }
        context.startActivity(intent)
    }
}
