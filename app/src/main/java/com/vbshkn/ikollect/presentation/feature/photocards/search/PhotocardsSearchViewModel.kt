package com.vbshkn.ikollect.presentation.feature.photocards.search

import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.domain.usecase.get.GetAllPhotocardsUseCase
import com.vbshkn.ikollect.presentation.feature.search.AbstractSearchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class PhotocardsSearchViewModel @Inject constructor(
    private val getAllPhotocardsUseCase: GetAllPhotocardsUseCase
) : AbstractSearchViewModel<PhotocardListItem>() {
    init {
        observeSearchResults(getAllPhotocardsUseCase())
    }
}
