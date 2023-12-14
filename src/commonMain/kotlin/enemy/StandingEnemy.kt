package enemy

import Enemy
import extensions.*
import getVectorTowardPoint
import korlibs.image.color.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.korge.view.collision.*
import manaSphere

class StandingEnemy(root: Container) : Enemy(root) {
    // randomly spawns does not move
    init {
        enemyShape = root.circle { radius = 32.0; color = Colors.BLUE }
        enemyShape.pos = randomPositionRect()
        enemyShape.onClick {
            damage(status.manaClickingPower)
            status.mana.sub(health)
        }

        updater = root.addUpdater {  }
    }
}

class MovingEnemy(root: Container) : Enemy(root) {


    init {

        enemyShape = root.circle { radius = 32.0; color = Colors.RED }
        enemyShape.pos = randomPositionPolar()

        enemyShape.onClick {
            damage(status.manaClickingPower)
            status.mana.sub(health)
        }
        updater = enemyShape.addUpdater { timespan ->

            if (enemyShape.collidesWithShape(manaSphere)) {
                // basically kill itself
                damage(100)
                status.mana.sub(damage)
            }
            val vector = getVectorTowardPoint(enemyShape.center().pos, manaSphere.center().pos)
//            val mag = vector.x.absoluteValue + vector.y.absoluteValue

            val movefactorVector = vector.normalized
            enemyShape.pos.let {
                x += movefactorVector.x * moveSpeedRatio * timespan.inWholeMilliseconds
                y += movefactorVector.y * moveSpeedRatio * timespan.inWholeMilliseconds
            }
        }
    }
}
