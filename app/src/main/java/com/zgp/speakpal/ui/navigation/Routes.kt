package com.zgp.speakpal.ui.navigation

object Routes {
    const val Home = "home"
    const val Words = "words"
    const val Practice = "practice"
    const val Result = "result"
    const val Records = "records"

    const val PracticeWithWord = "practice/{wordId}"
    const val ResultWithArgs = "result/{wordId}/{score}"

    fun practice(wordId: String) = "practice/$wordId"
    fun result(wordId: String, score: Int) = "result/$wordId/$score"
}
