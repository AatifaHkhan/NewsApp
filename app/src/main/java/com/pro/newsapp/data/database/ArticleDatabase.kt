package com.pro.newsapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pro.newsapp.data.database.dao.ArticleDao
import com.pro.newsapp.data.database.dao.SavedArticleDao
import com.pro.newsapp.data.database.entity.Article
import com.pro.newsapp.data.database.entity.SavedArticleEntity

@Database(entities = [SavedArticleEntity::class, Article::class], version = 1, exportSchema = false)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getSavedArticleDao(): SavedArticleDao
    abstract fun getArticleDao(): ArticleDao

}