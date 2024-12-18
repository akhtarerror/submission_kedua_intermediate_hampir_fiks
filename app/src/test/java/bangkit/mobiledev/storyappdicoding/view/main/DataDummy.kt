package bangkit.mobiledev.storyappdicoding.view.main

import bangkit.mobiledev.storyappdicoding.data.response.ListStoryItem
import java.text.SimpleDateFormat
import java.util.*

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "https://example.com/photo$i.jpg",  // Contoh URL foto
                createdAt = currentDate,  // Menggunakan tanggal saat ini
                name = "author + $i",  // Nama penulis
                description = "description for story $i",  // Deskripsi cerita
                lon = 106.8456,  // Contoh nilai longitude
                id = i.toString(),  // ID sebagai string
                lat = -6.2088  // Contoh nilai latitude
            )
            items.add(story)
        }
        return items
    }
}
