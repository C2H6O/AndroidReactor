package at.florianschuster.androidreactor

import io.reactivex.disposables.CompositeDisposable


/**
 * A ReactorView displays data. The view binds user inputs to the action stream and binds the view
 * states to each UI component. There's no business logic in a view layer. A view just defines how
 * to map the action stream and the state stream.
 *
 * Reference: https://github.com/ReactorKit/ReactorKit
 */
interface ReactorView<R : Reactor<*, *, *>> {
    /**
     * The CompositeDisposable that contains all view subscriptions.
     */
    val disposables: CompositeDisposable

    /**
     * The Reactor that binds to the View.
     */
    val reactor: R

    /**
     * Binds a Reactor to a View.
     */
    fun bind(reactor: R)
}