package com.vbshkn.ikollect.domain.usecase.update

import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.repository.TagRepository
import javax.inject.Inject

class UpdateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: TagItem) = tagRepository.update(tag)
}