package bangkit.mobiledev.storyappdicoding.view.main

import MainDispatcherRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
import bangkit.mobiledev.storyappdicoding.data.response.ListStoryItem
import bangkit.mobiledev.storyappdicoding.data.response.StoryResponse
import bangkit.mobiledev.storyappdicoding.view.adapter.StoryPagingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        // Preparing dummy data
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = PagingData.from(dummyStories)

        // Mocking repository to return dummy data wrapped in StoryResponse
        val storyResponse = StoryResponse(
            listStory = dummyStories,
            error = false,
            message = "Success"
        )
        val liveData = MutableLiveData<StoryResponse>()
        liveData.value = storyResponse
        Mockito.`when`(storyRepository.getAllStories(page = 1, size = 10)).thenReturn(liveData)

        // Create the ViewModel and observe the LiveData
        val mainViewModel = MainViewModel(storyRepository)

        // Observe LiveData that contains PagingData
        val actualStories = mainViewModel.stories.getOrAwaitValue()

        // Create a dummy diff callback for testing
        val mockDiffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }

        // Create AsyncPagingDataDiffer for comparison
        val differ = AsyncPagingDataDiffer(
            diffCallback = mockDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        // Assertions
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        // Preparing empty data
        val emptyStories = emptyList<ListStoryItem>()
        val data: PagingData<ListStoryItem> = PagingData.from(emptyStories)

        val storyResponse = StoryResponse(
            listStory = emptyStories,
            error = false,
            message = "No Data"
        )
        val liveData = MutableLiveData<StoryResponse>()
        liveData.value = storyResponse
        Mockito.`when`(storyRepository.getAllStories(page = 1, size = 10)).thenReturn(liveData)

        // Create the ViewModel and observe the LiveData
        val mainViewModel = MainViewModel(storyRepository)

        // Observe LiveData that contains PagingData
        val actualStories = mainViewModel.stories.getOrAwaitValue()

        // Create a dummy diff callback for testing
        val mockDiffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }

        // Create AsyncPagingDataDiffer for comparison
        val differ = AsyncPagingDataDiffer(
            diffCallback = mockDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        // Assertions for empty list
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

// Dummy ListUpdateCallback for PagingData
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
