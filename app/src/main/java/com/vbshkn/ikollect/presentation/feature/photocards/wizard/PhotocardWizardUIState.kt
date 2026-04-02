package com.vbshkn.ikollect.presentation.feature.photocards.wizard

import com.vbshkn.ikollect.domain.model.PhotocardCandidate

data class PhotocardWizardUIState(
    val currentStep: Int = 0,
    val dialogState: PhotocardWizardDialogState = PhotocardWizardDialogState.None,
    val photocardCandidate: PhotocardCandidate = PhotocardCandidate()
)
