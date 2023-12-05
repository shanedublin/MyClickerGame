import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.inject.*
import korlibs.io.lang.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.*

suspend fun main() = Korge(windowSize = Size(1200, 800), backgroundColor = Colors["#2b2b2b"]) {


    mainInjector.mapSingleton<Status> { Status(0) }

    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ SkillTreeScreen() })

}

val mainInjector = Injector()

class MyScene : Scene() {
    val status = mainInjector.get<Status>()
    val enemy = EnemyFactory()

    override suspend fun SContainer.sceneMain() {


        var updater = AutoClickUpdater(status, root)

        val t = text("Click on the circle")
        t.centerXOnStage()
        t.alignTopToTopOf(root, 10)

        val spendingPoints = text("Spending Points: ")
        val spendingPointsNumber = text("0")
        spendingPoints.alignTopToBottomOf(t, 10)
        spendingPoints.alignLeftToLeftOf(root, 10)
        spendingPointsNumber.alignTopToBottomOf(t, 10)
        spendingPointsNumber.alignLeftToRightOf(spendingPoints, 10)

        val investText = text("Investment: ")
        val investNumber = text("0")
        investText.alignTopToBottomOf(spendingPoints, 10)
        investText.alignLeftToLeftOf(root, 10)
        investNumber.alignTopToBottomOf(spendingPoints, 10)
        investNumber.alignLeftToRightOf(investText, 10)

        val expText = text("Exp: ")
        val expNumber = text("0")
        expText.alignTopToBottomOf(investText, 10)
        expText.alignLeftToLeftOf(root, 10)
        expNumber.alignTopToBottomOf(investText, 10)
        expNumber.alignLeftToRightOf(expText, 10)

        val resetButton = uiButton(label = "Reset")
        resetButton.alignLeftToLeftOf(root, 10)
        resetButton.alignBottomToBottomOf(root, 10)

        resetButton.onClick {
            status.reset()
            sceneContainer.changeTo({ SkillTreeScreen() })
        }

        val clickUpgradeButton = uiButton(label = "Click Power lvl: 1", Size(200, 32))
        clickUpgradeButton.alignRightToRightOf(root, 10)
        clickUpgradeButton.alignBottomToBottomOf(root, 10)

        clickUpgradeButton.onPress {
            status.clickingSkill.upgrade(status.spendingPoints)
            clickUpgradeButton.text = "${status.clickingSkill.name} lvl: ${status.clickingSkill.level}"
        }
        clickUpgradeButton.text = "${status.clickingSkill.name} lvl: ${status.clickingSkill.level}"

        val investUpgradeButton = uiButton(label = "Invest Power lvl: 1", Size(200, 32))
        investUpgradeButton.alignRightToRightOf(root, 10)
        investUpgradeButton.alignBottomToTopOf(clickUpgradeButton, 10)


        investUpgradeButton.onPress {
            status.investmentSkill.upgrade(status.spendingPoints)
            investUpgradeButton.text = "${status.investmentSkill.name} lvl: ${status.investmentSkill.level}"
        }
        investUpgradeButton.text = "${status.investmentSkill.name} lvl: ${status.investmentSkill.level}"

        val autoClickUpgradeButton = uiButton(label = "AutoClick lvl: 0", Size(200, 32))
        autoClickUpgradeButton.alignRightToRightOf(root, 10)
        autoClickUpgradeButton.alignBottomToTopOf(investUpgradeButton, 10)
        autoClickUpgradeButton.onPress {
            status.autoClickingSkill.upgrade(status.spendingPoints)
            autoClickUpgradeButton.text = "${status.autoClickingSkill.name} lvl: ${status.autoClickingSkill.level}"
            updater.resetUpdater()
        }
        autoClickUpgradeButton.text = "${status.autoClickingSkill.name} lvl: ${status.autoClickingSkill.level}"

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

