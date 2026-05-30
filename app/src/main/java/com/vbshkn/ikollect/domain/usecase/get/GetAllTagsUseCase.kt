package com.vbshkn.ikollect.domain.usecase.get

import com.vbshkn.ikollect.data.repository.TagRepositoryImpl
import com.vbshkn.ikollect.domain.repository.TagRepository
import javax.inject.Inject

class GetAllTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke() = tagRepository.getAll()
}