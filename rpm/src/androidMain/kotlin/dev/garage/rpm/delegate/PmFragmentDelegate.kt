package dev.garage.rpm.delegate

import android.os.Bundle
import androidx.fragment.app.Fragment
import dev.garage.rpm.PmView
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.navigation.FragmentNavigationMessageDispatcher

/**
 * Delegate for the [Fragment] that helps with creation and binding of
 * a [presentation model][PresentationModel] and a [view][PmView].
 *
 * Use this class only if you can't subclass the [PmFragment].
 *
 * Users of this class must forward all the lifecycle methods from the containing Fragment
 * to the corresponding ones in this class.
 */
class PmFragmentDelegate<PM, F>(
    private val pmFragment: F,
    private val retainMode: RetainMode
)
        where PM : PresentationModel,
              F : Fragment, F : PmView<PM> {

    /**
     * Strategies for retaining the PresentationModel[PresentationModel].
     * [SAVED_STATE] - the PresentationModel will be destroyed if the Activity is finishing or the Fragment state has not been saved.
     * [CONFIGURATION_CHANGES] - Retain the PresentationModel during a configuration change.
     */
    enum class RetainMode { SAVED_STATE, CONFIGURATION_CHANGES }

    private val commonDelegate = CommonDelegate<PM, F>(pmFragment,
        FragmentNavigationMessageDispatcher(pmFragment)
    )

    val presentationModel: PM get() = commonDelegate.presentationModel

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        commonDelegate.onCreate(savedInstanceState)
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onViewCreated(savedInstanceState: Bundle?) {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onActivityCreated(savedInstanceState: Bundle?) {
        commonDelegate.onBind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStart() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onResume() {
        commonDelegate.onResume()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onSaveInstanceState(outState: Bundle) {
        commonDelegate.onSaveInstanceState(outState)
        commonDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onPause() {
        commonDelegate.onPause()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onStop() {
        // For symmetry, may be used in the future
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroyView() {
        commonDelegate.onUnbind()
    }

    /**
     * You must call this method from the containing [Fragment]'s corresponding method.
     */
    fun onDestroy() {
        when (retainMode) {
            RetainMode.SAVED_STATE -> {
                if (pmFragment.activity?.isFinishing == true
                    || (pmFragment.fragmentManager?.isStateSaved?.not() == true)
                ) {
                    commonDelegate.onDestroy()
                }
            }

            RetainMode.CONFIGURATION_CHANGES -> {
                if (pmFragment.activity?.isChangingConfigurations?.not() == true) {
                    commonDelegate.onDestroy()
                }
            }
        }
    }
}