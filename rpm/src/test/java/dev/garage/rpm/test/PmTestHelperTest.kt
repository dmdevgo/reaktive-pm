package dev.garage.rpm.test

import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.nhaarman.mockitokotlin2.spy
import dev.garage.rpm.PresentationModel
import dev.garage.rpm.PresentationModel.Lifecycle.*
import dev.garage.rpm.test.PmTestHelper.LifecycleSteps
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class PmTestHelperTest {

    private lateinit var pmTestHelper: PmTestHelper
    private lateinit var lifecycleObserver: TestObservableObserver<PresentationModel.Lifecycle>

    @Before fun setUp() {
        val pm = spy<PresentationModel>()
        lifecycleObserver = pm.lifecycleObservable.test()
        pmTestHelper = PmTestHelper(pm)
    }

    @Test fun initToCreated() {
        pmTestHelper.setLifecycleTo(CREATED)

        lifecycleObserver.assertValues(CREATED)
    }

    @Test fun initToBinded() {
        pmTestHelper.setLifecycleTo(BINDED)

        lifecycleObserver.assertValues(CREATED, BINDED)
    }

    @Test fun initToResumed() {
        pmTestHelper.setLifecycleTo(RESUMED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED)
    }

    @Test fun initToPaused() {
        pmTestHelper.setLifecycleTo(PAUSED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED)
    }

    @Test fun initToUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED)
    }

    @Test fun initToDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test fun initToDestroyedAllSteps() {
        pmTestHelper.setLifecycleTo(DESTROYED, LifecycleSteps.ALL)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test fun initToDestroyedBypassResuming() {
        pmTestHelper.setLifecycleTo(DESTROYED, LifecycleSteps.BYPASS_RESUMING)

        lifecycleObserver.assertValues(CREATED, BINDED, UNBINDED, DESTROYED)
    }

    @Test fun initToDestroyedBypassBinding() {
        pmTestHelper.setLifecycleTo(DESTROYED, LifecycleSteps.BYPASS_BINDING)

        lifecycleObserver.assertValues(CREATED, DESTROYED)
    }

    @Test fun ignoreBypassResumingWhenInitToPaused() {
        pmTestHelper.setLifecycleTo(PAUSED, LifecycleSteps.BYPASS_RESUMING)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED)
    }

    @Test fun ignoreBypassBindingWhenInitToUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED, LifecycleSteps.BYPASS_BINDING)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED)
    }

    @Test fun setOneByOne() {
        pmTestHelper.setLifecycleTo(CREATED)
        pmTestHelper.setLifecycleTo(BINDED)
        pmTestHelper.setLifecycleTo(RESUMED)
        pmTestHelper.setLifecycleTo(PAUSED)
        pmTestHelper.setLifecycleTo(UNBINDED)
        pmTestHelper.setLifecycleTo(DESTROYED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test fun throwOnDuplicateState() {
        pmTestHelper.setLifecycleTo(CREATED)
        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is CREATED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterBinded() {
        pmTestHelper.setLifecycleTo(BINDED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is BINDED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterResumed() {
        pmTestHelper.setLifecycleTo(RESUMED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is RESUMED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterPaused() {
        pmTestHelper.setLifecycleTo(PAUSED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is PAUSED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is UNBINDED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun throwOnCreatedAfterDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as CREATED when it already is DESTROYED."
        ) {
            pmTestHelper.setLifecycleTo(CREATED)
        }
    }

    @Test fun setResumedAfterPaused() {
        pmTestHelper.setLifecycleTo(PAUSED)
        pmTestHelper.setLifecycleTo(RESUMED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, RESUMED)
    }

    @Test fun setBindedAfterUnbinded() {
        pmTestHelper.setLifecycleTo(UNBINDED)
        pmTestHelper.setLifecycleTo(BINDED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, BINDED)
    }

    @Test fun setMultipleResumedAndPaused() {
        pmTestHelper.setLifecycleTo(PAUSED)
        pmTestHelper.setLifecycleTo(RESUMED)
        pmTestHelper.setLifecycleTo(PAUSED)
        pmTestHelper.setLifecycleTo(RESUMED)
        pmTestHelper.setLifecycleTo(DESTROYED)

        lifecycleObserver.assertValues(
            CREATED,
            BINDED,
            RESUMED,
            PAUSED,
            RESUMED,
            PAUSED,
            RESUMED,
            PAUSED,
            UNBINDED,
            DESTROYED
        )
    }

    @Test fun throwOnBindedAfterDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as BINDED when it already is DESTROYED."
        ) {
            pmTestHelper.setLifecycleTo(BINDED)
        }
    }

    @Test fun throwOnUnbindedAfterDestroyed() {
        pmTestHelper.setLifecycleTo(DESTROYED)

        assertFailsWith<IllegalStateException>(
            "You can't set lifecycle state as UNBINDED when it already is DESTROYED."
        ) {
            pmTestHelper.setLifecycleTo(UNBINDED)
        }
    }
}