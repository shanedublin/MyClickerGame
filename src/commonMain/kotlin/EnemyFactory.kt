import korlibs.image.color.*
import korlibs.io.lang.*
import korlibs.korge.input.mouse
import korlibs.korge.view.*
import korlibs.korge.view.Circle
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.math.random.*
import kotlin.math.absoluteValue
import kotlin.random.*
import kotlin.time.*

class EnemyFactory {
    val list = mutableListOf<Enemy>()
    fun cleanup() {
        list.forEach {
            it.cleanup()
        }
    }

    fun create(root: Container) {
        list.add(Enemy(root))
    }
}

class Enemy(root: Container) {

    val updater: Cancellable
    val c: Circle

    init {
        c = root.circle { radius = 16.0; fill = Colors.RED; stroke = Colors.BLACK }
        var t = 0L
//        val randomPos = Point(Random(2L).ints(0,1024).first(),2)
//        c.centerOnStage()
//        c.pos = Point(0, 0)
//        val center = c.pos
//        println("Center pos: ${center}")
//        root.width
//        root.height
        // change to be the mouse?
        val center = Point(root.width/2,root.height/2)

        updater = c.addUpdater { timeSpan ->
            val target = stage?.input?.mousePos
            target?.let {

                val vector = getVectorTowardPoint(c.center().pos, target)
                val mag = vector.x.absoluteValue + vector.y.absoluteValue

                if(mag > 2){
                    val movefactorVector = vector.normalized
                    c.pos.let {
                        x += movefactorVector.x
                        y += movefactorVector.y
                    }

                } else{

                }

            }


            // this is for circleing
//            t += timeSpan.inWholeMilliseconds / 8
//            c.pos.let {
//                val deg = (t % 360).toInt()
//                val a = Angle.fromDegrees(deg)
//                x = a.cosine * 100 + center.x
//                y = a.sine * 100 + center.y
//            }
        }

    }


    fun cleanup() {
        updater.cancel()
        c.removeFromParent()
    }

    fun randomPosition(): Point {
        val random = Random(2L)
        val p1 = random[0, 1024]
        val p2 = random[0, 1024]
        return Point(p1, p2)
    }


    fun circle() {

    }
}


fun getVectorTowardPoint(from: Point, tooo: Point): Vector2D {
    val vector = tooo - from
    return vector

}
