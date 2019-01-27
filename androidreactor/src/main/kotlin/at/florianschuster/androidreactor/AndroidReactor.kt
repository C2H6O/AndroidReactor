package at.florianschuster.androidreactor


typealias ErrorHandler = (Throwable) -> Unit

/**
 * Object to handle Library initializations.
 */
object AndroidReactor {
    private var errorHandler: ErrorHandler? = null

    /**
     * Handles error messages, which are swallowed by the state stream by default.
     *
     * @param handler (Throwable) -> Unit
     */
    fun handleErrorsWith(handler: ErrorHandler) {
        this.errorHandler = handler
    }

    internal fun log(throwable: Throwable) {
        errorHandler?.invoke(throwable)
    }
}