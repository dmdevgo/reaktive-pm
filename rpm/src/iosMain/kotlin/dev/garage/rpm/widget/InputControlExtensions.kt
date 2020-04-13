package dev.garage.rpm.widget

import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.skip
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import dev.garage.rpm.PmView
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.focus
import dev.garage.rpm.bindings.focusChanges
import dev.garage.rpm.bindings.textChanges
import platform.UIKit.UITextField

/**
 * Binds the [InputControl] to the [UITextField][field], use it ONLY in [PmView.onBindPresentationModel].
 */
fun InputControl.bindTo(field: UITextField) {

    val editing = AtomicBoolean(false)

    text.bindTo {
        val editable = field.text
        if (it != editable) {
            editing.value = true
            field.text = it
            editing.value = false
        }
    }

    focus.bindTo(field.focus())

    field.textChanges()
        .skip(1)
        .filter { !editing.value && text.valueOrNull?.equals(it) != true }
        .map { it }
        .bindTo(textChanges)

    field.focusChanges().bindTo(focusChanges)
}

/**
 * Binds the [InputControl] to the [TextInputLayout][layout], use it ONLY in [PmView.onBindPresentationModel].
 */
fun InputControl.bindTo(layout: TextInputLayout) {
    bindTo(layout.getTextField())

    error.bindTo { error ->
        layout.setError(if (error.isEmpty()) null else error)
    }
}