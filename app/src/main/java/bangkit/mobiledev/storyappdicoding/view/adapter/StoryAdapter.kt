package bangkit.mobiledev.storyappdicoding.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import bangkit.mobiledev.storyappdicoding.data.response.ListStoryItem
import com.bumptech.glide.Glide
import bangkit.mobiledev.storyappdicoding.databinding.ItemStoryBinding

class StoryAdapter(
    private var stories: MutableList<ListStoryItem>, // Change to MutableList for dynamic updates
    private val onItemClick: (ListStoryItem, ActivityOptionsCompat) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    // Getter function to access stories
    fun getStories(): List<ListStoryItem> = stories

    // Function to update stories list
    fun updateStories(newStories: List<ListStoryItem>) {
        stories.clear() // Clear current list
        stories.addAll(newStories) // Add new stories
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.descriptionText.text = story.description
            Glide.with(binding.ivItemPhoto.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    Pair(binding.ivItemPhoto, "photo"),
                    Pair(binding.tvItemName, "name"),
                    Pair(binding.descriptionText, "description")
                )
                onItemClick(story, optionsCompat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size
}
