package com.vbshkn.ikollect.domain.usecase.delete

import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.repository.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: TagItem) = tagRepository.delete(tag)
}