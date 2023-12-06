import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.inject.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*

suspend fun main() = Korge(windowSize = Size(1400, 800), backgroundColor = Colors["#2b2b2b"]) {


    mainInjector.mapSingleton<Status> { Status(0) }

    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MyScene() })

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

        val manaText = text("Mana: ")
        val manaNumber = text("0")
        manaText.alignBottomToBottomOf(root, 10)
        manaText.centerXOnStage()
        manaNumber.alignBottomToBottomOf(root, 10)
        manaNumber.alignLeftToRightOf(manaText, 10)

        val resetButton = uiButton(label = "Reset")
        resetButton.alignLeftToLeftOf(root, 10)
        resetButton.alignBottomToBottomOf(root, 10)

        resetButton.onClick {
            status.reset()
            sceneContainer.changeTo({ SkillTreeScreen() })
        }


        // MANA REGEN Rate

        val clickUpgradeButton = uiButton(label = "Click Power lvl: 1", Size(400, 32))
        clickUpgradeButton.alignRightToRightOf(root, 10)
        clickUpgradeButton.alignBottomToBottomOf(root, 10)

        clickUpgradeButton.onPress {
            status.clickingSkill.upgrade(status.mana)
            clickUpgradeButton.text = status.clickingSkill.buttonDescrpition()

        }
        clickUpgradeButton.text = status.clickingSkill.buttonDescrpition()


        // INVEST

        val investUpgradeButton = uiButton(label = "Invest Power lvl: 1", Size(400, 32))
        investUpgradeButton.alignRightToRightOf(root, 10)
        investUpgradeButton.alignBottomToTopOf(clickUpgradeButton, 10)
        investUpgradeButton.onPress {
            status.investmentSkill.upgrade(status.mana)
            investUpgradeButton.text = status.investmentSkill.buttonDescrpition()
        }
        investUpgradeButton.text = status.investmentSkill.buttonDescrpition()


        // AUTO CLICKING

        val autoClickUpgradeButton = uiButton(label = "AutoClick lvl: 0", Size(400, 32))
        autoClickUpgradeButton.alignRightToRightOf(root, 10)
        autoClickUpgradeButton.alignBottomToTopOf(investUpgradeButton, 10)
        autoClickUpgradeButton.onPress {
            status.autoClickingSkill.upgrade(status.mana)
            autoClickUpgradeButton.text = status.autoClickingSkill.buttonDescrpition()
            updater.resetUpdater()
        }
        autoClickUpgradeButton.text = status.autoClickingSkill.buttonDescrpition()


        var expCircle = circle { radius = 30.0 }
        expCircle.centerOnStage()

        val investCircle = circle { radius = 10.0 }
        investCircle.alignTopToBottomOf(expCircle, 10)
        investCircle.centerXOnStage()

        investCircle.onClick { status.invest() }
        expCircle.onClick { status.clickMana() }

        this.addUpdater {
            manaNumber.text = "${status.mana}"
            // show the max number
            investNumber.text = "${status.investment} : ${status.maxInvestment}"
            spendingPointsNumber.text = "${status.spendingPoints}"
        }


//        enemy.create(root as Container)

    }

    override suspend fun sceneDestroy() {
        super.sceneDestroy()
        enemy.cleanup()
    }
}

