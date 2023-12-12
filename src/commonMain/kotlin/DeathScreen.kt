import korlibs.korge.input.onClick
import korlibs.korge.scene.Scene
import korlibs.korge.ui.uiButton
import korlibs.korge.ui.uiText
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.alignBottomToBottomOf
import korlibs.korge.view.align.centerOnStage
import korlibs.korge.view.align.centerXOnStage
import korlibs.korge.view.text

class DeathScreen : Scene() {

    val status = mainInjector.get<Status>()

    override suspend fun SContainer.sceneMain() {


        val deathText = text(getDeathText())
        deathText.centerOnStage()

        val restartButton = uiButton(label = "Try Again")

        restartButton.centerXOnStage()
        restartButton.alignBottomToBottomOf(root,20)

        restartButton.onClick {
            status.reset()
            enemyFactory.cleanup()
            sceneContainer.changeTo({ SkillTreeScreen() })
        }


    }




}


fun getDeathText(): String {

    return "You died :("

}
