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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.network.DevByteNetwork
import com.example.android.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Create a Repository to manage the offline cache in this app.
 *
 * The repository will have the logic to fetch the network results and to keep the database up-to-date.
 */
class VideosRepository(private val database: VideosDatabase) {

    /**
     * Retrieve data from the database
     *
     * Create a LiveData object to read the video playlist from the database. This LiveData object
     * is automatically updated when the database is updated.
     * Use Transformations.map to convert the list of database objects to a list
     * of domain objects.*/
    val videos: LiveData<List<DevByteVideo>> = Transformations.map(database.videoDao.getVideos()){
        it.asDomainModel()
    }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * Fetch the DevByte video playlist from the network using the Retrofit service instance,
     * DevByteNetwork.
     * Next, store the playlist in the Room database.
     */
    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {

            Timber.d("Refresh DevBytes videos is called");

            // Fetch the DevByte video playlist from the network
            val playlist = DevByteNetwork.devbytes.getPlaylist()

            // Store the video playlist in the Room database
            database.videoDao.insertVideos(playlist.asDatabaseModel())
        }
    }
}