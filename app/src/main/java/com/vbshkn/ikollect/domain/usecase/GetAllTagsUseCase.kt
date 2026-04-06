package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.data.repository.TagRepository
import javax.inject.Inject

class GetAllTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke() = tagRepository.getAll()
}