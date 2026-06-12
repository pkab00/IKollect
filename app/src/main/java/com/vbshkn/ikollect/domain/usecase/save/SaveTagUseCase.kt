package com.vbshkn.ikollect.domain.usecase.save

import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.repository.TagRepository
import javax.inject.Inject

class SaveTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: TagItem) = tagRepository.insert(tag)
}