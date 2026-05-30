package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.data.repository.ArtistRepositoryImpl
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetGroupMembersUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke(groupId: Long?) = if (groupId != null) {
        artistRepository.getGroupMembers(groupId)
    } else emptyFlow()
}