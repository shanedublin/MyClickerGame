import korlibs.io.lang.*
import korlibs.korge.view.*
import korlibs.time.*

class AutoClickUpdater(private val status: Status, private val root: View) {

    private lateinit var updater: Cancellable

    fun stop() {
        if (this::updater.isInitialized)
            updater.cancel()
    }

    fun resetUpdater() {
        if (this::updater.isInitialized)
            updater.cancel()
        if (status.autoClickingFrequency > 0) {
            updater = root.addFixedUpdater(Frequency(status.autoClickingFrequency), updatable = {
                status.clickMana()
            })
        }
    }
}
