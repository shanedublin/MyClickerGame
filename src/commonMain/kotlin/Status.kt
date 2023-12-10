class Status(var mana: Int = 0) {


    init {
        enemyDeathSignal.add {
            invest(it.enemy.reward)
        }
    }


    //    var mana = 0
    //var skillPoints = 0
    var manaPerSkillPoint = 10


    var maxMana = 10

    val clickingSkill = Skill("Clicking Power", upgradeAction = { cost -> manaClickingPower++; mana -= cost })
    val investmentSkill =
        Skill("Investment Power", upgradeAction = { cost -> investClickingPower += 2; mana -= cost })
    val autoClickingSkill = Skill(
        "Auto Clicking Frequency", costs = listOf(10, 100, 1000, 10000, 100000),
        upgradeAction = { cost -> autoClickingFrequency += 1; mana -= cost },
        level = 0
    )

    val manaIncreaseAmount = listOf(10, 30, 50, 100, 300)
    val increaseManaSkill =
        Skill("Increase Mana",
            level = 0,
            costs = listOf(10,20,400,1000),
            upgradeAction = { cost -> maxMana += getManaAmount(); spendingPoints -= cost })

    fun getManaAmount(): Int {
        return manaIncreaseAmount[increaseManaSkill.level-1]
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
        mana += manaClickingPower
        if (mana > maxMana) {
            mana = maxMana
        }
    }

    fun invest() {
        if (mana >= investClickingPower) {
            investment += investClickingPower
            mana -= investClickingPower
        } else if (mana > 0) {
            investment += mana
            mana = 0
        }
        if (investment >= maxInvestment) {
            investment -= maxInvestment
            spendingPoints += 1
            // This might need to change the scaling some how
            maxInvestment = (spendingPoints + 1) * manaPerSkillPoint
            invest(0)
        }
    }

    fun invest(investAmount: Int ){
        investment+= investAmount
        if (investment >= maxInvestment) {
            investment -= maxInvestment
            spendingPoints += 1
            invest(0)
            // This might need to change the scaling some how
            maxInvestment = (spendingPoints + 1) * manaPerSkillPoint
        }
    }


    fun reset() {
        mana = 0
        investment = 0
        if(spendingPoints < 0)
            spendingPoints = 0
        maxInvestment = spendingPoints + 1 * 10
    }



}

class Skill(
    var name: String = "Unnamed Skill",
    var level: Int = 1,
    var costs: List<Int> = listOf(0, 5, 10, 15, 20),
    var upgradeAction: (cost: Int) -> Unit = {}
) {
    /**
     *  returns if you can't upgrade or you are at max level
     */
    fun upgrade(availablePoints: Int): Int {
        val levelCost = costs.elementAtOrNull(level)
        levelCost?.let {
            if (it <= availablePoints) {
                level++
                upgradeAction(levelCost)
                println("Upgrading Skill $name to level : $level")
            }
        }
        return levelCost ?: 0
    }

    fun buttonDescrpition(): String {
        return "$name lvl: $level Upgrade Cost: ${upgrade(0)}"
    }
}
