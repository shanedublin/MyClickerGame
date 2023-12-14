import enemy.*
import korlibs.io.lang.*
import korlibs.korge.view.*
import korlibs.korge.view.Circle
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.random.*

class EnemyFactory(var root: View) {
    val status = mainInjector.get<Status>()
    val list = mutableListOf<Enemy>()
    val deleteMeList = mutableListOf<Enemy>()
    val updater: Cancellable
    val updater2: Cancellable


    init {

        enemyDeathSignal.add {
            //list.remove(it.enemy)
            deleteMeList.add(it.enemy)
        }


        updater = root.addFixedUpdater(.2.timesPerSecond) {

            spawnUnit()
            if (status.spendingPoints > 1) {
                spawnUnit()
            }
            if (status.spendingPoints > 2) {
                spawnUnit()
            }

        }

        updater2 = root.addUpdater {
            list.removeAll(deleteMeList)
            deleteMeList.clear()
        }
        spawnUnit()

    }


    fun cleanup() {
        list.forEach {
            it.cleanup()
        }
        updater.cancel()
        updater2.cancel()
    }

    fun create(root: Container): Enemy {
        return MovingEnemy(root).also { list.add(it) }
    }

    fun killUnit(enemy: Enemy) {
        list.remove(enemy)
    }

    fun spawnUnit() {
        val enemy = create(root as Container)
        enemy.enemyShape.pos = enemy.randomPositionPolar()
    }

}


abstract class Enemy(root: Container) {
    lateinit var updater: Cancellable
    lateinit var enemyShape: Circle
    val moveSpeedRatio = .1
    var reward = 5
    var health = 1
    var maxHealth = 1
    var damage = 2

    val status = mainInjector.get<Status>()


    fun cleanup() {
        updater.cancel()
        enemyShape.removeFromParent()
    }

    fun randomPositionPolar(length: Double = 700.0): Point {
        val angle = Random.nextInt()
        return Point.polar(manaSphere.pos, angle.degrees, length)
    }

    fun randomPositionRect(): Point {
        return Point(Random.nextInt(0, 1400), Random.nextInt(0, 800))
    }


    fun circle() {
        // this is for circleing
//            t += timeSpan.inWholeMilliseconds / 8
//            c.pos.let {
//                val deg = (t % 360).toInt()
//                val a = Angle.fromDegrees(deg)
//                x = a.cosine * 100 + center.x
//                y = a.sine * 100 + center.y
//            }
    }

    fun kill() {
        cleanup()
        enemyDeathSignal.invoke(EnemyDeath(this))
    }

    fun damage(i: Int): Int {
        health -= i
        if (health <= 0) {
            kill()
        }
        return 3
    }
}


fun getVectorTowardPoint(from: Point, tooo: Point): Vector2D {
    val vector = tooo - from
    return vector

}
