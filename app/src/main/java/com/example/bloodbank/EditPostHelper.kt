package com.example.bloodbank

import android.app.Activity
import android.content.Context
import android.content.Intent

class EditPostHelper(private val context: Context) {

    fun showEditPostDialog(activity: Activity, post: UserPost, requestCode: Int) {
        val intent = Intent(context, EditPostActivity::class.java).apply {
            putExtra("post", post)
        }
        activity.startActivityForResult(intent, requestCode)
    }
}
