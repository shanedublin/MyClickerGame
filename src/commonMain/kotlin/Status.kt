import korlibs.io.concurrent.atomic.KorAtomicInt
import korlibs.io.concurrent.atomic.setValue
import kotlin.reflect.KProperty


class Status() {
    var mana: KorAtomicInt = KorAtomicInt(0)


    init {
        enemyDeathSignal.add {
            invest(it.enemy.reward)
        }
    }


    //    var mana = 0
    //var skillPoints = 0
    var manaPerSkillPoint = 10


    var maxMana = 10

    val clickingSkill = Skill("Clicking Power",
        upgradeAction = { cost -> manaClickingPower++; mana.addAndGet(-cost) },
        resetAction = { manaClickingPower = 1; })

    val investmentSkill = Skill("Investment Power",
        upgradeAction = { cost -> investClickingPower += 2; mana.addAndGet(-cost) },
        resetAction = { investClickingPower = 2 })
    val autoClickingSkill = Skill(
        "Auto Clicking Frequency",
        costs = listOf(10, 100, 1000, 10000, 100000),
        upgradeAction = { cost -> autoClickingFrequency += 1; mana.addAndGet(-cost) },
        resetAction = { autoClickingFrequency = 0.0 },
        initialLevel = 0
    )

    val manaIncreaseAmount = listOf(10, 30, 50, 100, 300)
    val increaseManaSkill =
        Skill("Increase Mana",
            initialLevel = 0,
            costs = listOf(5, 10, 20, 100),
            upgradeAction = { cost -> maxMana += getManaAmount(); spendingPoints -= cost })

    fun getManaAmount(): Int {
        return manaIncreaseAmount[increaseManaSkill.currentLevel - 1]
    }


    var manaClickingPower = 1
    var investClickingPower = 2
    var autoClickingFrequency = 0.0

    var investment = 0
    var maxInvestment = 10

    /**
     * also known as skill points
     */
    var spendingPoints = 0


    fun clickMana() {
        mana.addAndGet(manaClickingPower)
        if (mana.value > maxMana) {
            mana.compareAndSet(mana.value, maxMana)
        }
    }

    fun invest() {
        if (mana.value >= investClickingPower) {
            investment += investClickingPower
            mana.addAndGet(-investClickingPower)
        } else if (mana.value > 0) {
            investment += mana.value
            mana.compareAndSet(mana.value, 0)
        }
        if (investment >= maxInvestment) {
            investment -= maxInvestment
            spendingPoints += 1
            // This might need to change the scaling some how
            maxInvestment = (spendingPoints + 1) * manaPerSkillPoint
            invest(0)
        }
    }

    fun invest(investAmount: Int) {
        investment += investAmount
        if (investment >= maxInvestment) {
            investment -= maxInvestment
            spendingPoints += 1
            invest(0)
            // This might need to change the scaling some how
            maxInvestment = (spendingPoints + 1) * manaPerSkillPoint
        }
    }


    fun reset() {
        mana.compareAndSet(mana.value, 0)
        investment = 0
        if (spendingPoints < 0)
            spendingPoints = 0
        maxInvestment = spendingPoints + 1 * 10


        // skills
        autoClickingSkill.reset()
        clickingSkill.reset()

    }


}

class Skill(
    var name: String = "Unnamed Skill",
    initialLevel: Int = 1,
    var costs: List<Int> = listOf(0, 5, 10, 15, 20),
    var upgradeAction: (cost: Int) -> Unit = {},
    var resetAction: () -> Unit = {}
) {
    var currentLevel: Int = 0
    var initialLevel: Int = 0

    init {
        currentLevel = initialLevel
        this.initialLevel = initialLevel
    }

    /**
     *  returns if you can't upgrade or you are at max level
     */
    fun upgrade(availablePoints: Int): Int {
        val levelCost = costs.elementAtOrNull(currentLevel)
        levelCost?.let {
            if (it <= availablePoints) {
                currentLevel++
                upgradeAction(levelCost)
                println("Upgrading Skill $name to level : $currentLevel")
            }
        }
        return levelCost ?: 0
    }

    fun reset() {
        resetAction()
        currentLevel = initialLevel
    }

    fun buttonDescrpition(): String {
        return "$name lvl: $currentLevel Upgrade Cost: ${upgrade(0)}"
    }
}
