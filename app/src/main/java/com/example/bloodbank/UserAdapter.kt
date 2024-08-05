package com.example.bloodbank

import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(private val userList: MutableList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_card, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount() = userList.size


    // The respective views from the user_card are being populated by the users data fetched from the database
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val bloodGroupTextView: TextView = itemView.findViewById(R.id.bloodGroupTextView)
        private val provinceTextView: TextView = itemView.findViewById(R.id.provinceTextView)

        fun bind(user: User) {
            nameTextView.text = "${user.firstName} ${user.lastName}"
            bloodGroupTextView.text = "Type: ${user.bloodGroup}"
            provinceTextView.text = user.province

// Uncomment the below line when we actually get a URL for the profile pic from firebase
//            Picasso.get().load(user.profileImageUrl).into(profileImageView)
            if (user.profilePic.isNotEmpty()) {
                Glide.with(itemView.context).load(user.profilePic).into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile_placeholder) // Replace with your placeholder image resource ID
            }

            itemView.setOnClickListener {
                Log.d("UserAdapter", "Item clicked: ${user.firstName} ${user.lastName}")
                val context = it.context
                val intent = Intent(context, DetailedUserProfileActivity::class.java).apply {
                    putExtra("firstName", user.firstName)
                    putExtra("lastName", user.lastName)
                    putExtra("dob", user.dob)
                    putExtra("bloodGroup", user.bloodGroup)
                    putExtra("city", user.city)
                    putExtra("province", user.province)
                    putExtra("country", user.country)
                    putExtra("email", user.email)
                    putExtra("phoneNumber", user.phoneNumber)
                    putExtra("isActive", user.isActive)
                    putExtra("profilePic", user.profilePic)
                }
                context.startActivity(intent)
            }
        }
    }
}
