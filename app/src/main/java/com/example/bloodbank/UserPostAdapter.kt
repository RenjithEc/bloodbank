import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbank.R
import com.example.blooddonationapp.UserPost

class UserPostAdapter(private val userList: List<UserPost>) : RecyclerView.Adapter<UserPostAdapter.UserViewHolder>() {

    // This uses user_card_post layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_card_post, parent, false)
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
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)

        fun bind(user: UserPost) {
            nameTextView.text = "${user.firstName} ${user.lastName}"
            bloodGroupTextView.text = user.bloodGroup
            descriptionTextView.text = user.description
            priorityTextView.text = "Priority: ${user.priority}"
            provinceTextView.text = user.province

// Uncomment the below line when we actually get a URL for the profile pic from firebase
//            Picasso.get().load(user.profileImageUrl).into(profileImageView)
        }
    }
}
