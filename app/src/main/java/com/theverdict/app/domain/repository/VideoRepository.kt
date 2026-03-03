package com.theverdict.app.domain.repository

import com.theverdict.app.domain.model.VideoChallenge

interface VideoRepository {
    suspend fun getRandomChallenge(): VideoChallenge
    suspend fun getChallengeById(id: String): VideoChallenge?
    fun getAllChallenges(): List<VideoChallenge>
    suspend fun getDailyCaseChallenge(): VideoChallenge
}
