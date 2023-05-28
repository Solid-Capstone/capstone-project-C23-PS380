package com.solidcapstone.semar.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.solidcapstone.semar.data.Result
import com.solidcapstone.semar.data.local.entity.VideoEntity
import com.solidcapstone.semar.data.local.entity.WayangEntity
import com.solidcapstone.semar.data.local.room.WayangDatabase
import com.solidcapstone.semar.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class WayangRepository private constructor(
    private val apiService: ApiService,
    private val database: WayangDatabase,
) {
    fun getListWayang(showLimit: Int? = null) = liveData {
        emit(Result.Loading)
        try {
            val listWayangResponse = apiService.getListWayang()

            // Save with DAO
            val listWayang = listWayangResponse.map { wayangResponse ->
                WayangEntity(
                    wayangResponse.id,
                    wayangResponse.name,
                    wayangResponse.photoUrl,
                    wayangResponse.description,
                )
            }
            database.wayangDao().deleteAllWayangs()
            database.wayangDao().insertWayangs(listWayang)
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }

        // Get wayang list from local database
        val localData: LiveData<Result<List<WayangEntity>>> = if (showLimit != null) {
            database.wayangDao().getListWayang(showLimit).map { Result.Success(it) }
        } else {
            database.wayangDao().getListWayang().map { Result.Success(it) }
        }
        emitSource(localData)
    }

    fun getWayang(id: Int) = liveData {
        emit(Result.Loading)
        try {
            val requestBody = id.toString().toRequestBody("text/plain".toMediaType())
            val wayangResponse = apiService.getWayang(requestBody)
            val wayangEntity = WayangEntity(
                wayangResponse.id,
                wayangResponse.name,
                wayangResponse.photoUrl,
                wayangResponse.description,
            )
            database.wayangDao().insertWayangs(listOf(wayangEntity))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }

        val localData: LiveData<Result<WayangEntity>> =
            database.wayangDao().getWayang(id).map { Result.Success(it) }
        emitSource(localData)
    }

    fun predictWayang(imgMultipart: MultipartBody.Part) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.predictWayang(imgMultipart)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getListVideo(showLimit: Int? = null) = liveData {
        emit(Result.Loading)
        try {
            val listVideoResponse = apiService.getListVideo()

            // Save with DAO
            val listVideo = listVideoResponse.map { videoResponse ->
                VideoEntity(
                    videoResponse.id,
                    videoResponse.name,
                    videoResponse.photoUrl,
                    videoResponse.videoDuration,
                    videoResponse.videoUrl,
                )
            }
            database.videoDao().deleteAllVideos()
            database.videoDao().insertVideos(listVideo)
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }

        // Get wayang list from local database
        val localData: LiveData<Result<List<VideoEntity>>> = if (showLimit != null) {
            database.videoDao().getListVideo(showLimit).map { Result.Success(it) }
        } else {
            database.videoDao().getListVideo().map { Result.Success(it) }
        }
        emitSource(localData)
    }

    fun getVideo(id: Int) = liveData {
        emit(Result.Loading)
        try {
            val requestBody = id.toString().toRequestBody("text/plain".toMediaType())
            val videoResponse = apiService.getVideo(requestBody)
            val videoEntity = VideoEntity(
                videoResponse.id,
                videoResponse.name,
                videoResponse.photoUrl,
                videoResponse.videoDuration,
                videoResponse.videoUrl,
            )
            database.videoDao().insertVideos(listOf(videoEntity))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }

        val localData: LiveData<Result<VideoEntity>> =
            database.videoDao().getVideo(id).map { Result.Success(it) }
        emitSource(localData)
    }


    companion object {
        @Volatile
        private var instance: WayangRepository? = null

        fun getInstance(
            apiService: ApiService,
            database: WayangDatabase,
        ): WayangRepository = instance ?: synchronized(this) {
            instance ?: WayangRepository(apiService, database)
        }.also { instance = it }
    }
}