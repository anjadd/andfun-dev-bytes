/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interface VideoDao with two helper methods to access the database:
 * save and load data from database.
 **/
@Dao
interface VideoDao {

    /**
     * The method getVideos() fetches all the videos from the database.
    */
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    /**
     * The method insertAll() inserts a list of videos fetched from the network into the database.
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideos(videos: List<DatabaseVideo>)
}

/**
 * Add the database for your offline cache by implementing RoomDatabase
 * */
@Database(entities = [DatabaseVideo::class], version = 1, exportSchema = false)
abstract class VideosDatabase : RoomDatabase() {
    abstract val videoDao: VideoDao
}

/**
 * Private variable INSTANCE to hold an instance of the database as a singleton object.
 */
private lateinit var INSTANCE: VideosDatabase

/**
 * Method getDatabase() initializes and returns the database INSTANCE variable.
 */
fun getDatabase(context: Context): VideosDatabase {
    synchronized(VideosDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                VideosDatabase::class.java,
                "videos"
            ).build()
        }
    }
    return INSTANCE
}