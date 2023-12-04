import korlibs.image.color.*
import korlibs.image.paint.*
import korlibs.io.lang.*
import korlibs.korge.view.*
import korlibs.korge.view.Circle
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.time.*

class EnemyFactory {
    val list = mutableListOf<Enemy>()
    fun cleanup(){
        list.forEach {
            it.cleanup()
        }
    }
    fun create(root: Container){
        list.add(Enemy(root))
    }
}

class Enemy(root:Container){

    val updater: Cancellable
    val c: Circle
    init {
        c = root.circle { radius = 16.0; fill = Colors.RED; stroke = Colors.BLACK }
        var t = 0L
        c.centerOnStage()
        val center = c.pos
        println("Center pos: ${center}")
         updater = c.addUpdater { timeSpan ->
            t += timeSpan.inWholeMilliseconds / 8
            c.pos.let {
                val deg = (t % 360).toInt()
                val a = Angle.fromDegrees(deg)
                x = a.cosine * 100 + center.x
                y = a.sine * 100 + center.y
            }
        }

    }


    fun cleanup() {
        updater.cancel()
        c.removeFromParent()
    }
}
