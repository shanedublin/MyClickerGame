import extensions.sub
import korlibs.event.Event
import korlibs.image.color.Colors
import korlibs.io.async.Signal
import korlibs.io.concurrent.atomic.KorAtomicInt
import korlibs.io.lang.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.korge.view.Circle
import korlibs.korge.view.collision.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.math.absoluteValue
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


        updater = root.addFixedUpdater(1.timesPerSecond) {

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
        return Enemy(root).also { list.add(it) }
    }

    fun killUnit(enemy: Enemy) {
        list.remove(enemy)
    }

    fun spawnUnit() {
        val enemy = create(root as Container)
        enemy.enemyShape.pos = enemy.randomPosition()
        enemy.enemyShape.onClick {
            enemy.kill()
        }
    }

}



class Enemy(root: Container) {
    val updater: Cancellable
    val enemyShape: Circle
    val moveSpeedRatio = 1
    val reward = 5
    var health = 1
    var maxHealth = 1
    var damage = 1

    val status = mainInjector.get<Status>()


    init {


        enemyShape = root.circle { radius = 16.0; color = Colors.RED }
        var t = 0L
//        val randomPos = Point(Random(2L).ints(0,1024).first(),2)
//        c.centerOnStage()
//        c.pos = Point(0, 0)
//        val center = c.pos
//        println("Center pos: ${center}")
//        root.width
//        root.height

        // change to be the mouse?

        updater = enemyShape.addUpdater { timeSpan ->

            if (enemyShape.collidesWithShape(manaSphere)) {
                // basically kill itself
                damage(100)
                status.mana.sub(damage)

            }

            val target = stage?.input?.mousePos

            target?.let {

                val vector = getVectorTowardPoint(enemyShape.center().pos, manaSphere.center().pos)
                val mag = vector.x.absoluteValue + vector.y.absoluteValue

//                x = target.x
//                y = target.y

                if (mag > 2) {
                    val movefactorVector = vector.normalized
                    enemyShape.pos.let {
                        x += movefactorVector.x * moveSpeedRatio
                        y += movefactorVector.y * moveSpeedRatio
                    }

                } else {
                    // do nothing
                }
            }

        }


    }


    fun cleanup() {
        updater.cancel()
        enemyShape.removeFromParent()
    }

    fun randomPosition(): Point {
        val angle = Random.nextInt()
//        val distance = Random.nextDouble(100.0,400.0)
        return Point.polar(manaSphere.pos, angle.degrees, 700.0)
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

    fun damage(i: Int) {
        health -= i
        if (health <= 0) {
            kill()
        }
    }
}


fun getVectorTowardPoint(from: Point, tooo: Point): Vector2D {
    val vector = tooo - from
    return vector

}
