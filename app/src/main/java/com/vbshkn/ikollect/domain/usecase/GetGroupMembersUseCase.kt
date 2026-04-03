package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.ArtistRepository
import javax.inject.Inject

class GetGroupMembersUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke(groupId: Long) = artistRepository.getGroupMembers(groupId)
}