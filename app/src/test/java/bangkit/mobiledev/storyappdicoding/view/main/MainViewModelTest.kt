package bangkit.mobiledev.storyappdicoding.view.main

import MainDispatcherRule
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import bangkit.mobiledev.storyappdicoding.data.repository.StoryRepository
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
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import bangkit.mobiledev.storyappdicoding.data.repository.UserRepository
import bangkit.mobiledev.storyappdicoding.database.entity.StoryEntity
import com.dicoding.myunlimitedquotes.getOrAwaitValue
import org.junit.After
import org.junit.Before
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockedStatic

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setup() {
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Boolean> { Log.isLoggable(anyString(), anyInt()) }.thenReturn(true)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getPagingStory()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(storyRepository, userRepository)
        val actualStories: PagingData<StoryEntity> = mainViewModel.getPagingStories().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when Get Story Data Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = data
        Mockito.`when`(storyRepository.getPagingStory()).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(storyRepository, userRepository)
        val actualStories: PagingData<StoryEntity> = mainViewModel.getPagingStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
        companion object {
            fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }
    }
}