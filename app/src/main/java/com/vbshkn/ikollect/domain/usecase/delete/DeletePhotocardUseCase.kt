package com.vbshkn.ikollect.domain.usecase.delete

import com.vbshkn.ikollect.data.repository.PhotocardRepositoryImpl
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import javax.inject.Inject

class DeletePhotocardUseCase @Inject constructor(
    private val photocardRepository: PhotocardRepository
) {
    suspend operator fun invoke(id: Long) {
        photocardRepository.softDelete(id)
    }
}