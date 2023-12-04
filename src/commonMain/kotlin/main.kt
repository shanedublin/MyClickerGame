import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.io.lang.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.*

suspend fun main() = Korge(windowSize = Size(512, 512), backgroundColor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MyScene() })
}


class MyScene : Scene() {
    val status = Status(0)
    val enemy = EnemyFactory()

    override suspend fun SContainer.sceneMain() {



        var updater = AutoClickUpdater(status,root)

        val t = text("Click on the circle")
        t.centerXOnStage()
        t.alignTopToTopOf(root, 10)

        val spendingPoints = text("Spending Points: ")
        val spendingPointsNumber = text("0")
        spendingPoints.alignTopToBottomOf(t, 10)
        spendingPointsNumber.alignTopToBottomOf(t, 10)
        spendingPointsNumber.alignLeftToRightOf(spendingPoints, 10)

        val investText = text("Investment: ")
        val investNumber = text("0")
        investText.alignTopToBottomOf(spendingPoints, 10)
        investNumber.alignTopToBottomOf(spendingPoints, 10)
        investNumber.alignLeftToRightOf(investText)

        val expText = text("Exp: ")
        val expNumber = text("0")
        expText.alignTopToBottomOf(investText, 10)
        expNumber.alignTopToBottomOf(investText, 10)
        expNumber.alignLeftToRightOf(expText)

        val resetButton = uiButton(label = "Reset")
        resetButton.alignLeftToLeftOf(root, 10)
        resetButton.alignBottomToBottomOf(root, 10)

        resetButton.onPress {
            status.reset()
        }

        val clickUpgradeButton = uiButton(label = "Click Power lvl: 1", Size(200, 32))
        clickUpgradeButton.alignRightToRightOf(root, 10)
        clickUpgradeButton.alignBottomToBottomOf(root, 10)

        clickUpgradeButton.onPress {
            status.clickingSkill.upgrade(status.spendingPoints)
            clickUpgradeButton.text = "${status.clickingSkill.name} lvl: ${status.clickingSkill.level}"
        }

        val investUpgradeButton = uiButton(label = "Invest Power lvl: 1", Size(200, 32))
        investUpgradeButton.alignRightToRightOf(root, 10)
        investUpgradeButton.alignBottomToTopOf(clickUpgradeButton, 10)


        investUpgradeButton.onPress {
            status.investmentSkill.upgrade(status.spendingPoints)
            investUpgradeButton.text = "${status.investmentSkill.name} lvl: ${status.investmentSkill.level}"
        }

        val autoClickUpgradeButton = uiButton(label = "AutoClick lvl: 0", Size(200, 32))
        autoClickUpgradeButton.alignRightToRightOf(root, 10)
        autoClickUpgradeButton.alignBottomToTopOf(investUpgradeButton, 10)
        autoClickUpgradeButton.onPress {
            status.autoClickingSkill.upgrade(status.spendingPoints)
            autoClickUpgradeButton.text = "${status.autoClickingSkill.name} lvl: ${status.autoClickingSkill.level}"
            updater.resetUpdater()
        }

        var expCircle = circle { radius = 30.0 }
        expCircle.centerOnStage()

        val investCircle = circle { radius = 10.0 }
        investCircle.alignTopToBottomOf(expCircle, 10)
        investCircle.centerXOnStage()

        investCircle.onClick { status.invest() }
        expCircle.onClick { status.clickExp() }

        this.addUpdater {
            expNumber.text = "${status.exp}"
            investNumber.text = "${status.investment}"
            spendingPointsNumber.text = "${status.spendingPoints}"
        }



        enemy.create(root as Container)

    }
    override suspend fun sceneDestroy() {
        super.sceneDestroy()
        enemy.cleanup()
    }
}

