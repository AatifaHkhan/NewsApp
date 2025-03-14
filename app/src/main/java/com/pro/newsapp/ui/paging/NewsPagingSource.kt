package com.pro.newsapp.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pro.newsapp.common.Const
import com.pro.newsapp.common.NoInternetException
import com.pro.newsapp.common.dispatcher.DispatcherProvider
import com.pro.newsapp.common.networkhelper.NetworkHelper
import com.pro.newsapp.data.database.entity.Article
import com.pro.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsPagingSource @Inject constructor(
    private val newsRepository: NewsRepository,
    private val networkHelper: NetworkHelper,
    private val dispatcherProvider: DispatcherProvider
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        lateinit var loadResult: LoadResult<Int, Article>

        withContext(dispatcherProvider.io) {
            kotlin.runCatching {
                if (!networkHelper.isNetworkConnected()) {
                    if (page == Const.DEFAULT_PAGE_NUM) {
                        val articles = newsRepository.getNewsFromDb()
                        loadResult = LoadResult.Page(
                            data = articles,
                            prevKey = page.minus(1),
                            nextKey = if (articles.isEmpty()) null else page.plus(1)
                        )
                    } else {
                        throw NoInternetException()
                    }
                } else {
                    val articles = newsRepository.getNews(page)
                    loadResult = LoadResult.Page(
                        data = articles,
                        prevKey = if (page == 1) null else page.minus(1),
                        nextKey = if (articles.isEmpty()) null else page.plus(1)
                    )
                }
            }.onFailure {
                loadResult = LoadResult.Error(it)
            }
        }
        return loadResult
    }
}