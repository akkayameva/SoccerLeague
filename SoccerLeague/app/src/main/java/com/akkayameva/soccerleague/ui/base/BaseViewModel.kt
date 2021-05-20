package com.akkayameva.soccerleague.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.system.measureTimeMillis

open class BaseViewModel : ViewModel() {

    companion object {
        val TAG: String = BaseViewModel::class.java.simpleName
    }

    fun <T> runAsync2(
        result: MutableLiveData<ApiResultUIModel<T>>,
        function: suspend () -> T
    ): Job {

        return viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {

            withContext(Dispatchers.Main) {
                emitApiResultUiState(showProgress = true, which = result)
            }

            measureTimeMillis {

                runCatching { return@runCatching function() }
                    .onSuccess {
                        emitApiResultUiState(
                            showProgress = false,
                            showSuccess = Event(Result.Success(it)),
                            which = result
                        )
                    }
                    .onFailure {
                        it.printStackTrace()
                        when (it) {
                            is SoccerThrowable -> {
                                emitApiResultUiState(
                                    showSuccess = Event(Result.Error(it)),
                                    which = result
                                )
                            }
                            else -> {
                                emitApiResultUiState(
                                    showSuccess = Event(Result.Error(SoccerThrowable(errorMessage = it.message))),
                                    which = result
                                )
                            }
                        }

                    }

            }.let {
                Timber.tag(TAG)
                    .e("Time taken by %s : %s (ms)", function.javaClass.enclosingMethod?.name, it)
            }

        }
    }


    private fun <T> emitApiResultUiState(
        showProgress: Boolean = false,
        showSuccess: Event<Result<T>> = Event(Result.Error(SoccerThrowable(errorMessage = "Unable to connect to server"))),
        which: MutableLiveData<ApiResultUIModel<T>>
    ) {

        val uiModel = ApiResultUIModel(
            showProgress = showProgress,
            showSuccess = showSuccess
        )
        which.postValue(uiModel)
    }
}

data class ApiResultUIModel<out T : Any?>(
    val showProgress: Boolean,
    val showSuccess: Event<Result<T>>
)

class SoccerThrowable(val errorCode: Int? = 0, errorMessage: String? = null) :
    Throwable(errorMessage)



