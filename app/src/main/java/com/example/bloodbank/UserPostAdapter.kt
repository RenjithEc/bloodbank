import android.annotation.SuppressLint
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbank.R
import com.example.bloodbank.UserPost
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class UserPostAdapter(private val userList: List<UserPost >,private val loggedInUserId: String) : RecyclerView.Adapter<UserPostAdapter.UserViewHolder>() {

    // This uses user_card_post layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_card_post, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user,loggedInUserId)
    }

    override fun getItemCount() = userList.size

    // The respective views from the user_card are being populated by the users data fetched from the database
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val bloodGroupTextView: TextView = itemView.findViewById(R.id.bloodGroupTextView)
        private val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
        private val detailsIcon: ImageView = itemView.findViewById(R.id.detailsIcon)
        private val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        private val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)


        @SuppressLint("SetTextI18n")
        fun bind(user: UserPost, loggedInUserId: String) {
            nameTextView.text = "${user.firstName} ${user.lastName}"
            bloodGroupTextView.text = user.bloodGroup
            descriptionTextView.text = user.description
            priorityTextView.text = "Priority: ${user.priority}"
            cityTextView.text = user.city

            // Calculate priority
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val needByDateTime = LocalDateTime.parse(user.needByDate.toString(), formatter)
            val daysUntilNeed = ChronoUnit.DAYS.between(currentDateTime, needByDateTime).toInt()

            val (priorityText, priorityColor) = when {
                daysUntilNeed < 2 -> "High ($daysUntilNeed days)" to R.color.redBright
                daysUntilNeed in 2..5 -> "Medium ($daysUntilNeed days)" to R.color.priority_medium
                else -> "Low ($daysUntilNeed days)" to R.color.halfWhite
            }
            priorityTextView.text = "Priority: $priorityText"
            priorityTextView.setTextColor(ContextCompat.getColor(itemView.context, priorityColor))

            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)

            if (user.userId == loggedInUserId) {
                constraintSet.setVisibility(deleteIcon.id,ConstraintSet.VISIBLE )
                constraintSet.setVisibility(editIcon.id,ConstraintSet.VISIBLE )
                constraintSet.clear(detailsIcon.id, ConstraintSet.BOTTOM)
                constraintSet.connect(detailsIcon.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect(detailsIcon.id, ConstraintSet.BOTTOM, deleteIcon.id, ConstraintSet.TOP)
                constraintSet.setVerticalBias(detailsIcon.id, 0.075f)
            } else {
                constraintSet.setVisibility(deleteIcon.id,ConstraintSet.GONE )
                constraintSet.setVisibility(editIcon.id,ConstraintSet.GONE )
                constraintSet.clear(detailsIcon.id, ConstraintSet.TOP)
                constraintSet.clear(detailsIcon.id, ConstraintSet.BOTTOM)
                constraintSet.connect(detailsIcon.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect(detailsIcon.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.setVerticalBias(detailsIcon.id, 0.5f)
            }

            constraintSet.applyTo(constraintLayout)

// Uncomment the below line when we actually get a URL for the profile pic from firebase
//            Picasso.get().load(user.profileImageUrl).into(profileImageView)
        }
    }
}
