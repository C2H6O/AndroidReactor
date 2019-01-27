package at.florianschuster.savestatesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.florianschuster.androidreactor.ReactorView
import at.florianschuster.androidreactor.ViewModelReactor
import at.florianschuster.androidreactor.viewModelReactor
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.visibility
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_saved_state.*
import java.util.concurrent.TimeUnit


const val layout = R.layout.activity_saved_state
const val counterValueKey = "SaveStateActivity.counter"

class SaveStateActivity : AppCompatActivity(), ReactorView<SaveStateReactor> {
    private var counterValue: Int = 0
    override val reactor by viewModelReactor { SaveStateReactor(counterValue) }
    override val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        counterValue = savedInstanceState?.getInt(counterValueKey) ?: 0
        bind(reactor)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(counterValueKey, reactor.currentState.value)
    }

    override fun bind(reactor: SaveStateReactor) {
        btnIncrease.clicks()
            .map { SaveStateReactor.Action.Increase }
            .subscribe(reactor.action)
            .let(disposables::add)

        btnDecrease.clicks()
            .map { SaveStateReactor.Action.Decrease }
            .subscribe(reactor.action)
            .let(disposables::add)

        reactor.state.map { it.value }
            .distinctUntilChanged()
            .map { "$it" }
            .subscribe(tvValue::setText)
            .let(disposables::add)

        reactor.state.map { it.loading }
            .distinctUntilChanged()
            .subscribe(progressLoading.visibility())
            .let(disposables::add)
    }
}

class SaveStateReactor(
    initialValue: Int
) : ViewModelReactor<SaveStateReactor.Action, SaveStateReactor.Mutation, SaveStateReactor.State>(State(initialValue)) {
    sealed class Action {
        object Increase : Action()
        object Decrease : Action()
    }

    sealed class Mutation {
        object IncreaseValue : Mutation()
        object DecreaseValue : Mutation()
        data class SetLoading(val loading: Boolean) : Mutation()
    }

    data class State(
        val value: Int,
        val loading: Boolean = false
    )

    override fun mutate(action: Action): Observable<out Mutation> = when (action) {
        is Action.Increase -> Observable.concat(
            Observable.just(Mutation.SetLoading(true)),
            Observable.just(Mutation.IncreaseValue).delay(500, TimeUnit.MILLISECONDS),
            Observable.just(Mutation.SetLoading(false))
        )
        is Action.Decrease -> Observable.concat(
            Observable.just(Mutation.SetLoading(true)),
            Observable.just(Mutation.DecreaseValue).delay(500, TimeUnit.MILLISECONDS),
            Observable.just(Mutation.SetLoading(false))
        )
    }

    override fun reduce(state: State, mutation: Mutation): State = when (mutation) {
        is Mutation.IncreaseValue -> state.copy(value = state.value + 1)
        is Mutation.DecreaseValue -> state.copy(value = state.value - 1)
        is Mutation.SetLoading -> state.copy(loading = mutation.loading)
    }
}