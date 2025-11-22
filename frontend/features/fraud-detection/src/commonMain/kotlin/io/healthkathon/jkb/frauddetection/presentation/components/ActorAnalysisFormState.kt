package io.healthkathon.jkb.frauddetection.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.healthkathon.jkb.frauddetection.presentation.ActorType

@Stable
class ActorAnalysisFormState(
    initialActorType: ActorType = ActorType.DOCTOR,
    initialSelectedActor: String = ""
) {
    var selectedActorType by mutableStateOf(initialActorType)
    var selectedActor by mutableStateOf(initialSelectedActor)
    var actorExpanded by mutableStateOf(false)

    val isFormValid: Boolean
        get() = selectedActor.isNotBlank()

    fun reset() {
        selectedActorType = ActorType.DOCTOR
        selectedActor = ""
        actorExpanded = false
    }

    fun changeActorType(actorType: ActorType) {
        selectedActorType = actorType
        selectedActor = ""
        actorExpanded = false
    }
}

@Composable
fun rememberActorAnalysisFormState(
    initialActorType: ActorType = ActorType.DOCTOR,
    initialSelectedActor: String = ""
): ActorAnalysisFormState = remember {
    ActorAnalysisFormState(initialActorType, initialSelectedActor)
}
