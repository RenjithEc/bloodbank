import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bloodbank.R
import com.example.bloodbank.UserPost
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.example.bloodbank.DeletePostActivity
import com.example.bloodbank.DetailedUserPostActivity
import com.example.bloodbank.EditPostHelper

class UserPostAdapter(private val userList: List<UserPost>, private val loggedInUserId: String, private val fromPage: String) : RecyclerView.Adapter<UserPostAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_card_post, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, loggedInUserId)
    }

    override fun getItemCount() = userList.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val bloodGroupTextView: TextView = itemView.findViewById(R.id.bloodGroupTextView)
        private val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        private val seeMore: ImageView = itemView.findViewById(R.id.vertMoreDots)

        @SuppressLint("SetTextI18n")
        fun bind(user: UserPost, loggedInUserId: String) {
            nameTextView.text = "${user.firstName} ${user.lastName}"
            bloodGroupTextView.text = "Type: ${user.bloodGroup}"
            descriptionTextView.text = user.description
            priorityTextView.text = "Priority: ${user.priority}"
            cityTextView.text = user.city

            // Calculate priority
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val needByDateTime = LocalDateTime.parse(user.needByDate.toString(), formatter)
            val daysUntilNeed = ChronoUnit.DAYS.between(currentDateTime, needByDateTime).toInt() + 1

            val (priorityText, priorityColor) = when {
                daysUntilNeed <= 2 -> "High ($daysUntilNeed days)" to R.color.redBright
                daysUntilNeed in 3..5 -> "Medium ($daysUntilNeed days)" to R.color.priority_medium
                else -> "Low ($daysUntilNeed days)" to R.color.halfWhite
            }
            priorityTextView.text = "Priority: $priorityText"
            priorityTextView.setTextColor(ContextCompat.getColor(itemView.context, priorityColor))

            // Uncomment the below line when we actually get a URL for the profile pic from firebase
            // Load profile picture or placeholder
            if (user.profileImageUrl.isNotEmpty()) {
                Glide.with(itemView.context).load(user.profileImageUrl).into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile_placeholder) // Replace with your placeholder image resource ID
            }

            // Should allow user to delete or update the post only from home page
            if (user.userId == loggedInUserId && fromPage == "HomeActivity") {
                seeMore.visibility = View.VISIBLE
                seeMore.setOnClickListener {
                    showCustomPopupMenu(it, itemView.context, user)
                }
            } else {
                seeMore.visibility = View.GONE
            }
            itemView.setOnClickListener {
                Log.d("UserAdapter", "Item clicked: ${user.firstName} ${user.lastName}")
                val context = it.context
                val intent = Intent(context, DetailedUserPostActivity::class.java).apply {
                    putExtra("firstName", user.firstName)
                    putExtra("lastName", user.lastName)
                    putExtra("bloodGroup", user.bloodGroup)
                    putExtra("city", user.city)
                    putExtra("province", user.province)
                    putExtra("country", user.country)
                    putExtra("email", user.email)
                    putExtra("phoneNumber", user.phone)
                    putExtra("profilePic", user.profileImageUrl)
                    putExtra("needByDate", user.needByDate)
                    putExtra("description",user.description)
                    putExtra("priority", priorityTextView.text)
                    putExtra("needByDate",needByDateTime.toString())
                }
                context.startActivity(intent)
              }


        }

        private fun showCustomPopupMenu(anchor: View, context: Context, user: UserPost) {
            val gradientDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16f
                setColor(Color.parseColor("#222222"))
                setStroke(2, Color.parseColor("#F57578"))
            }

            val popupLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = gradientDrawable
                setPadding(2, 2, 2, 2)
            }

            val editTextView = TextView(context).apply {
                text = context.getString(R.string.editPostPopUp)
                textSize = 14f
                setTextColor(Color.parseColor("#F57578"))
                setBackgroundResource(R.drawable.card_border)
                setPadding(42, 32, 42, 32)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            val deleteTextView = TextView(context).apply {
                text = context.getString(R.string.deletePostPopUp)
                textSize = 14f
                setTextColor(Color.parseColor("#F57578"))
                setBackgroundResource(R.drawable.card_border)
                setPadding(42, 32, 42, 32)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            popupLayout.apply {
                addView(editTextView)
                addView(deleteTextView)
            }

            // A parent container is added so that enough margin can be given as padding to the right
            val containerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 0, 32, 0)
                setBackgroundColor(Color.TRANSPARENT)
                addView(popupLayout)
            }

            val popupWindow = PopupWindow(containerLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            val xOff = -50
            popupWindow.showAsDropDown(anchor, xOff, 0)

            deleteTextView.setOnClickListener {
                val postDeletionHelper = DeletePostActivity(context)
                postDeletionHelper.showDeleteConfirmationDialog(user) {
                    (userList as MutableList).remove(user)
                    notifyDataSetChanged()
                }
                popupWindow.dismiss()
            }

            editTextView.setOnClickListener {
                val editPostHelper = EditPostHelper(context)
                editPostHelper.showEditPostDialog(context as Activity, user, 2)
                popupWindow.dismiss()
            }

        }
    }
}