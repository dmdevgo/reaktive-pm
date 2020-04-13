package dev.garage.rpm

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.publish
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.subject.publish.PublishSubject
import dev.garage.rpm.util.ConsumerWrapper

/**
 * Reactive property for the commands to the [view][PmView].
 * Can be observed and changed in reactive manner with it's [observable] and [PresentationModel.consumer].
 *
 * Use to represent a command to the view, e.g. toast or dialog showing.
 *
 * @param isIdle observable, that shows when `command` need to buffer the values (while isIdle value is true).
 * Buffered values will be delivered later (when isIdle emits false).
 * By default (when null is passed) it will buffer while the [view][PmView] is paused.
 *
 * @param bufferSize how many values should be kept in buffer. Null means no restrictions.
 *
 * @see Action
 * @see Command
 */
class Command<T> internal constructor(
    internal val pm: PresentationModel,
    isIdle: Observable<Boolean>? = null,
    bufferSize: Int? = null
) {
    internal val relay = PublishSubject<T>()

    /**
     * Observable of this [Command].
     */
    val observable: Observable<T> =
        if (bufferSize == 0) {
            relay
        } else {
            if (isIdle == null) {
                relay.bufferWhileIdle(pm.paused, bufferSize)
            } else {
                relay.bufferWhileIdle(isIdle, bufferSize)
            }
        }
            .publish()
            .apply { connect() }
}

/**
 * Creates the [Command].
 *
 * @param isIdle observable, that shows when `command` need to buffer the values (while isIdle value is true).
 * Buffered values will be delivered later (when isIdle emits false).
 * By default (when null is passed) it will buffer while the [view][PmView] is unbind from the [PresentationModel].
 *
 * @param bufferSize how many values should be kept in buffer. Null means no restrictions.
 */
fun <T> PresentationModel.command(
    isIdle: Observable<Boolean>? = null,
    bufferSize: Int? = null
): Command<T> {

    return Command(this, isIdle, bufferSize)
}

/**
 * Subscribes to the [Command][Command] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
fun <T> Command<T>.bindTo(consumer: Consumer<in T>) {
    with(pm) {
        this@bindTo.observable
            .subscribe(consumer)
            .untilUnbind()
    }
}

/**
 * Subscribe to the [Command][Command] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
fun <T> Command<T>.bindTo(consumer: (T) -> Unit) {
    with(pm) {
        this@bindTo.observable
            .subscribe(onNext = consumer)
            .untilUnbind()
    }
}

/**
 * Subscribes to the [Command][Command] and adds it to the subscriptions list
 * that will be CLEARED ON [UNBIND][PresentationModel.Lifecycle.UNBINDED],
 * so use it ONLY in [PmView.onBindPresentationModel].
 */
fun <T> Command<T>.bindTo(consumer: ConsumerWrapper<in T>) {
    with(pm) {
        this@bindTo.observable
            .subscribe(consumer)
            .untilUnbind()
    }
}