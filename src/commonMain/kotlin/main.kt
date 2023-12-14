import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.inject.*
import korlibs.io.async.launchImmediately
import korlibs.io.lang.Cancellable
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.Circle
import korlibs.korge.view.align.*
import korlibs.math.geom.*

suspend fun main() = Korge(windowSize = Size(1400, 800), backgroundColor = Colors["#2b2b2b"]) {


    mainInjector.mapSingleton<Status> { Status() }

    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MyScene() })

}

val mainInjector = Injector()
lateinit var enemyFactory: EnemyFactory
lateinit var manaSphere: Circle


class Player {


}

class MyScene : Scene() {
    val status = mainInjector.get<Status>()
    var autoClickUpdater: AutoClickUpdater? = null


    override suspend fun sceneBeforeLeaving() {
        super.sceneBeforeLeaving()
        autoClickUpdater?.stop()
    }

    override suspend fun SContainer.sceneMain() {


        manaSphere = circle {
            radius = 64.0
            onClick {
                status.clickMana()
            }
            pos = (Point(700, 400))
        }

        val investCircle = circle { radius = 10.0 }
        investCircle.alignTopToBottomOf(manaSphere, 10)
        investCircle.centerXOnStage()

        investCircle.onClick { status.invest() }
//        manaSphere.onClick { status.clickMana() }
        // should rearange so this isntt dependent on manasphere being initialized
        enemyFactory = EnemyFactory(root)


        autoClickUpdater = AutoClickUpdater(status, root)

        val t = text("Click on the circle")
        t.centerXOnStage()
        t.alignTopToTopOf(root, 10)

        val spendingPoints = text("Skill Points: ")
        val spendingPointsNumber = text("0")
        spendingPointsNumber.alignTopToBottomOf(t, 10)
        spendingPointsNumber.alignRightToRightOf(root, 40)
        spendingPoints.alignTopToBottomOf(t, 10)
        spendingPoints.alignRightToRightOf(spendingPointsNumber, 10)

        val investText = text("Investment: ")
        val investNumber = text("0")
        investText.alignTopToBottomOf(t, 10)
        investText.alignLeftToLeftOf(root, 10)
        investNumber.alignTopToBottomOf(t, 10)
        investNumber.alignLeftToRightOf(investText, 10)

        val manaHealthBar = roundRect(Size(200, 40), RectCorners(4), Colors.AQUA)
        manaHealthBar.alignBottomToBottomOf(root, 10)
        manaHealthBar.centerXOnStage()


        val manaText = text("Mana: ", textSize = Text.DEFAULT_TEXT_SIZE * 1.6)
        val manaNumber = text("0", textSize = Text.DEFAULT_TEXT_SIZE * 1.6)
        manaText.centerOn(manaHealthBar)

        manaNumber.alignBottomToBottomOf(manaText)
        manaNumber.alignLeftToRightOf(manaText, 10)

        manaHealthBar.width = 10.0



        val resetButton = uiButton(label = "Reset")
        resetButton.alignLeftToLeftOf(root, 10)
        resetButton.alignBottomToBottomOf(root, 10)

        resetButton.onClick {
            status.reset()
            enemyFactory.cleanup()
            sceneContainer.changeTo({ SkillTreeScreen() })
        }


        // MANA REGEN Rate

        val clickUpgradeButton = uiButton(label = "Click Power lvl: 1", Size(400, 32))
        clickUpgradeButton.alignRightToRightOf(root, 10)
        clickUpgradeButton.alignBottomToBottomOf(root, 10)

        clickUpgradeButton.onPress {
            status.clickingSkill.upgrade(status.mana.value)
            clickUpgradeButton.text = status.clickingSkill.buttonDescrpition()

        }
        clickUpgradeButton.text = status.clickingSkill.buttonDescrpition()


        // INVEST

        val investUpgradeButton = uiButton(label = "Invest Power lvl: 1", Size(400, 32))
        investUpgradeButton.alignRightToRightOf(root, 10)
        investUpgradeButton.alignBottomToTopOf(clickUpgradeButton, 10)
        investUpgradeButton.onPress {
            status.investmentSkill.upgrade(status.mana.value)
            investUpgradeButton.text = status.investmentSkill.buttonDescrpition()
        }
        investUpgradeButton.text = status.investmentSkill.buttonDescrpition()


        // AUTO CLICKING

        val autoClickUpgradeButton = uiButton(label = "AutoClick lvl: 0", Size(400, 32))
        autoClickUpgradeButton.alignRightToRightOf(root, 10)
        autoClickUpgradeButton.alignBottomToTopOf(investUpgradeButton, 10)
        autoClickUpgradeButton.onPress {
            status.autoClickingSkill.upgrade(status.mana.value)
            autoClickUpgradeButton.text = status.autoClickingSkill.buttonDescrpition()
            autoClickUpdater?.resetUpdater()
        }
        autoClickUpgradeButton.text = status.autoClickingSkill.buttonDescrpition()


        mouse.onClick { it ->
            val point = it.downPosStage
            println(enemyFactory.list.size)

            val iter = enemyFactory.list.iterator()
            while (iter.hasNext()) {
                val enemy = iter.next()
                if (enemy.enemyShape.hitTestAny(point)) {
                    enemy.damage(100)
                }

            }

        }

        this.addUpdater {

            val target = input.mousePos
            val buttons: Int = input.mouseButtons


            // check player health
            if (status.mana.value < 0) {
                status.reset()
                launchImmediately {
                    sceneContainer.changeTo { DeathScreen() }
                }

            }


            // Update Text last
            manaNumber.text = "${status.mana}"
            // show the max number
            investNumber.text = "${status.investment} : ${status.maxInvestment}"
            spendingPointsNumber.text = "${status.spendingPoints}"

            manaHealthBar.width = 10.0 + 190.0 * status.mana.value.toDouble() / status.maxMana.toDouble()


        }


//        enemy.create(root as Container)

    }

    override suspend fun sceneDestroy() {
        super.sceneDestroy()
        enemyFactory.cleanup()
    }
}

