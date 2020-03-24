package dev.garage.rpm

import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import dev.garage.rpm.PresentationModel.Lifecycle
import dev.garage.rpm.PresentationModel.Lifecycle.*
import dev.garage.rpm.util.SchedulersRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PresentationModelTest {

    @get:Rule
    val schedulers = SchedulersRule()

    private lateinit var lifecycleCallbacks: LifecycleCallbacks
    private lateinit var pm: TestPm
    private lateinit var lifecycleObserver: TestObservableObserver<Lifecycle>

    @Before
    fun setUp() {
        lifecycleCallbacks = mock()
        pm = TestPm(lifecycleCallbacks)
        lifecycleObserver = pm.lifecycleObservable.test()
    }

    @Test
    fun observingLifecycle() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        lifecycleObserver.assertValues(CREATED, BINDED, RESUMED, PAUSED, UNBINDED, DESTROYED)
    }

    @Test
    fun invokeLifecycleCallbacks() {
        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        verify(lifecycleCallbacks).onCreate()
        verify(lifecycleCallbacks).onBind()
        verify(lifecycleCallbacks).onResume()
        verify(lifecycleCallbacks).onPause()
        verify(lifecycleCallbacks).onUnbind()
        verify(lifecycleCallbacks).onDestroy()
    }

    @Test
    fun invokeLifecycleCallbacksWhenChildAttached() {
        val childPm = spy<PresentationModel>()
        childPm.attachToParent(pm)

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)

        verify(lifecycleCallbacks).onCreate()
        verify(lifecycleCallbacks).onBind()
        verify(lifecycleCallbacks).onResume()
        verify(lifecycleCallbacks).onPause()
        verify(lifecycleCallbacks).onUnbind()
        verify(lifecycleCallbacks).onDestroy()
    }

    @Test
    fun currentLifecycleValue() {
        assertNull(pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(CREATED)
        assertEquals(CREATED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(BINDED)
        assertEquals(BINDED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(RESUMED)
        assertEquals(RESUMED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(PAUSED)
        assertEquals(PAUSED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(UNBINDED)
        assertEquals(UNBINDED, pm.currentLifecycleState)

        pm.lifecycleConsumer.accept(DESTROYED)
        assertEquals(DESTROYED, pm.currentLifecycleState)
    }

    @Test
    fun commandBlocksItemsBeforeCreated() {
        val testObserver = pm.commands.observable.test()

        pm.acceptCommand(1)

        testObserver.assertNoValues()
    }

    @Test
    fun commandBlocksItemsBeforeBinded() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.acceptCommand(1)

        testObserver.assertNoValues()
    }

    @Test
    fun commandBlocksItemsBeforeResumed() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)

        pm.acceptCommand(1)

        testObserver.assertNoValues()
    }

    @Test
    fun commandPassItemsWhenResumed() {
        val testObserver = pm.commands.observable.test()
        val testLifecycle = pm.lifecycleObservable.test()
        val testIdle = pm.paused.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.acceptCommand(1)
        pm.acceptCommand(2)

        testIdle.assertValues(true, false)
        testLifecycle.assertValues(CREATED, BINDED, RESUMED)
        testObserver.assertOnlyValues(1, 2)
    }

    @Test
    fun commandSendsBufferedItemsAfterResumed() {
        val testObserver = pm.commands.observable.test()

        pm.acceptCommand(1)
        pm.lifecycleConsumer.accept(CREATED)
        pm.acceptCommand(2)
        pm.lifecycleConsumer.accept(BINDED)
        pm.acceptCommand(3)
        pm.lifecycleConsumer.accept(RESUMED)

        testObserver.assertValues(1, 2, 3)
    }

    @Test
    fun commandBlocksItemsAfterPaused() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.acceptCommand(1)

        testObserver.assertNoValues()
    }

    @Test
    fun commandSendsBufferedItemsAfterResumedAgain() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.acceptCommand(1)
        pm.acceptCommand(2)
        pm.lifecycleConsumer.accept(RESUMED)

        testObserver.assertValues(1, 2)
    }

    @Test
    fun commandBlocksItemsAfterDestroyed() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        pm.lifecycleConsumer.accept(UNBINDED)
        pm.lifecycleConsumer.accept(DESTROYED)
        pm.acceptCommand(1)

        testObserver.assertNoValues()
    }

    @Test
    fun commandSendsBufferedItemsAfterResubscribedAndResumedAgain() {
        val testObserver = pm.commands.observable.test()

        pm.lifecycleConsumer.accept(CREATED)
        pm.lifecycleConsumer.accept(BINDED)
        pm.lifecycleConsumer.accept(RESUMED)
        pm.lifecycleConsumer.accept(PAUSED)
        testObserver.dispose()

        pm.acceptCommand(1)

        val testObserver2 = pm.commands.observable.test()
        pm.lifecycleConsumer.accept(RESUMED)

        testObserver.assertNoValues()
        testObserver2.assertValues(1)
    }
}

open class TestPm(private val callbacks: LifecycleCallbacks) : PresentationModel() {

    val commands = command<Int>()

    fun acceptCommand(i: Int) {
        commands.consumer.accept(i)
    }

    override fun onCreate() {
        callbacks.onCreate()
    }

    override fun onBind() {
        callbacks.onBind()
    }

    override fun onResume() {
        callbacks.onResume()
    }

    override fun onPause() {
        callbacks.onPause()
    }

    override fun onUnbind() {
        callbacks.onUnbind()
    }

    override fun onDestroy() {
        callbacks.onDestroy()
    }
}

interface LifecycleCallbacks {
    fun onCreate() {}
    fun onBind() {}
    fun onResume() {}
    fun onPause() {}
    fun onUnbind() {}
    fun onDestroy() {}
}