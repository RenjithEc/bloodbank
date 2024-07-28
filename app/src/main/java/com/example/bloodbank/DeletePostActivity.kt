package com.example.bloodbank

import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class DeletePostActivity(private val context: Context) {

    fun showDeleteConfirmationDialog(user: UserPost, onSuccess: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_delete, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.dialog_title).text = context.getString(R.string.deleteDialogTitle)
        dialogView.findViewById<TextView>(R.id.dialog_message).text =context.getString(R.string.deleteDialogText)

        dialogView.findViewById<Button>(R.id.dialog_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.dialog_confirm).setOnClickListener {
            deletePost(user) {
                onSuccess()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun deletePost(user: UserPost, onSuccess: () -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("posts").document(user.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error deleting post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
