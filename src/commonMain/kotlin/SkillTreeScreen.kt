import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*

class SkillTreeScreen :Scene(){

    val status = mainInjector.get<Status>()

    override suspend fun SContainer.sceneMain() {


        val skillTreeText = text("Skill Tree")
        skillTreeText.centerXOnStage()
        skillTreeText.alignTopToTopOf(root,10)

        val skillPointsText = text("Skill Points: ${status.spendingPoints}")
        skillPointsText.centerXOnStage()
        skillPointsText.alignTopToBottomOf(skillTreeText,10)


        val resetButton = uiButton(label = "Reset")
        resetButton.alignLeftToLeftOf(root, 10)
        resetButton.alignBottomToBottomOf(root, 10)


        val skillButton = uiButton(label = "Increase Maximum Mana", size= Size(200,100))
        skillButton.text = status.increaseManaSkill.buttonDescrpition()
        skillButton.onClick {
            status.increaseManaSkill.upgrade(status.spendingPoints)
            skillButton.text = status.increaseManaSkill.buttonDescrpition()
        }

        skillButton.alignLeftToLeftOf(root,10)
        skillButton.alignTopToTopOf(root,60)

        resetButton.onClick {
            sceneContainer.changeTo({MyScene()})
        }


    }
}
