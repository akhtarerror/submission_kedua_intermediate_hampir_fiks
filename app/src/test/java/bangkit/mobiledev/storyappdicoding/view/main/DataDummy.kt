package bangkit.mobiledev.storyappdicoding.view.main

import bangkit.mobiledev.storyappdicoding.database.entity.StoryEntity

object DataDummy {
    fun generateDummyStoryEntity(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..10) {
            val story = StoryEntity(
                id = "story-$i",
                name = "Story Name $i",
                description = "Description $i",
                photoUrl = "https://picsum.photos/200/300?random=$i",
                createdAt = "2023-01-01T00:00:00Z",
                lat = null,
                lon = null
            )
            items.add(story)
        }
        return items
    }
}