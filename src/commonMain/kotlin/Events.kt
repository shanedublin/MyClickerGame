import korlibs.event.Event
import korlibs.event.EventType
import korlibs.io.async.Signal


class EnemyDeath(val enemy: Enemy)
val enemyDeathSignal = Signal<EnemyDeath>()
