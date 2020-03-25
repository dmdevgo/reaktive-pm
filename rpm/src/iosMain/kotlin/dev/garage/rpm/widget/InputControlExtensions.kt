package dev.garage.rpm.widget

import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.skip
import dev.garage.rpm.PmView
import dev.garage.rpm.bindTo
import dev.garage.rpm.bindings.focus
import dev.garage.rpm.bindings.focusChanges
import dev.garage.rpm.bindings.textChanges
import platform.UIKit.UITextField

/**
 * Binds the [InputControl] to the [EditText][editText], use it ONLY in [PmView.onBindPresentationModel].
 */
fun InputControl.bindTo(textField: UITextField) {

    var editing = false

    text.bindTo {
        val editable = textField.text
        if (it != editable) {
            editing = true
            textField.text = editable
            editing = false
        }
    }

    focus.bindTo(textField.focus())

    textField.textChanges()
        .skip(1)
        .filter { !editing && text.valueOrNull?.equals(it) != true }
        .map { it }
        .bindTo(textChanges)

    textField.focusChanges().bindTo(focusChanges)
}