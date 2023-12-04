class Status(var exp: Int = 0) {

    val maxExp = 1000000

    val clickingSkill = Skill("Clicking Power", upgradeAction = { cost -> expClickingPower++; spendingPoints -= cost })
    val investmentSkill = Skill("Investment Power", upgradeAction = { cost ->  investClickingPower += 2; spendingPoints -= cost })
    val autoClickingSkill = Skill("Auto Clicking Frequency", costs = listOf(10,100,1000,10000,100000,),
        upgradeAction = { cost ->  autoClickingFrequency += 1; spendingPoints -= cost },
        level = 0)

    var expClickingPower = 1
    var investClickingPower = 2
    var autoClickingFrequency = 0.0

    var investment = 0
    var maxInvestment = 100

    var spendingPoints = 50
    var maxSpendingPoints = 100


    fun clickExp() {
        exp += expClickingPower
        if (exp > maxExp) {
            exp = maxExp
        }
    }

    fun invest() {
        if (exp >= investClickingPower) {
            investment += investClickingPower
            exp -= investClickingPower
        } else if (exp > 0) {
            investment += exp
            exp = 0
        }
        if (investment > maxInvestment) {
            investment = maxInvestment
        }
    }

    fun reset() {
        spendingPoints += investment
        if (spendingPoints > maxSpendingPoints) {
            spendingPoints = maxSpendingPoints
        }
        exp = 0
        investment = 0
    }
}

class Skill(
    var name: String = "Unnamed Skill",
    var level: Int = 1,
    var costs: List<Int> = listOf(10, 11, 12, 13),
    var upgradeAction: (cost: Int) -> Unit = {}
) {
    /**
     *  returns if you can't upgrade or you are at max level
     */
    fun upgrade(availablePoints: Int): Int {
        val levelCost = costs.elementAtOrNull(level - 1)
        levelCost?.let {
            if (it <= availablePoints) {
                level++
                upgradeAction(levelCost)
                println("Upgrading Skill $name to level : $level")
            }
        }
        return levelCost ?: 0
    }
}
