import java.util.*

val n = 100_000_000
val r = 0.5
val p1 = 0.6
val p2 = 0.4

val random = Random()

fun isP1(): Boolean = random.nextDouble() <= p1
fun isP2(): Boolean = random.nextDouble() <= p2

enum class StateP1 {
    IDLE, PROC, BLOCK
}

fun main() {

    var p010 = 0
    var p001 = 0
    var p201 = 0
    var p110 = 0
    var p011 = 0
    var p111 = 0
    var p101 = 0

    var processedCount = 0
    var blockCount = 0
    var refusedCount = 0
    var totalCount = 0
    var queueCounter = 0
    var reqsNowCounter = 0
    var reqProcSystemCounter = 0

    val reqProcSystemQueue = ArrayDeque<Int>()

    var stateP1 = StateP1.IDLE
    var queueSlots = 1
    var isP2Proc = false

    fun isR(): Boolean {
        return (random.nextDouble() <= r).also {
            if (!it) {
                totalCount++
            }
        }
    }

    fun tryGetNew(index: Int) {
        if (!isR()) {
            stateP1 = StateP1.PROC
            reqProcSystemQueue.add(index)
        }
    }

    fun tryAddTo2(index: Int) {
        if (queueSlots == 1) {
            if (isP2Proc) {
                queueSlots--
            } else {
                isP2Proc = true
            }
            stateP1 = StateP1.IDLE
            tryGetNew(index)
        } else {
            stateP1 = StateP1.BLOCK
        }
    }

    repeat(n) { index ->
        when {
            stateP1 == StateP1.IDLE && queueSlots == 1 && !isP2Proc -> p010++
            stateP1 == StateP1.IDLE && queueSlots == 0 && isP2Proc -> p001++
            stateP1 == StateP1.BLOCK && queueSlots == 0 && isP2Proc -> p201++
            stateP1 == StateP1.PROC && queueSlots == 1 && !isP2Proc -> p110++
            stateP1 == StateP1.IDLE && queueSlots == 1 && isP2Proc -> p011++
            stateP1 == StateP1.PROC && queueSlots == 1 && isP2Proc -> p111++
            stateP1 == StateP1.PROC && queueSlots == 0 && isP2Proc -> p101++
            else -> println("wtf")
        }

        if (stateP1 == StateP1.BLOCK) {
            blockCount++
        }
        queueCounter += 1 - queueSlots

        if (stateP1 != StateP1.IDLE) {
            reqsNowCounter++
        }
        reqsNowCounter += 1 - queueSlots
        if (isP2Proc) {
            reqsNowCounter++
        }

        if (queueSlots < 1) {
            if (isP2Proc) {
                if (!isP2()) {
                    reqProcSystemCounter += index - reqProcSystemQueue.poll()!!
                    processedCount++
                    queueSlots++
                }
            } else {
                isP2Proc = true
                queueSlots++
            }
        } else {
            if (isP2Proc && !isP2()) {
                reqProcSystemCounter += index - reqProcSystemQueue.poll()!!
                processedCount++
                isP2Proc = false
            }
        }

        when (stateP1) {
            StateP1.IDLE -> {
                tryGetNew(index)
            }
            StateP1.PROC -> {
                if (!isP1()) {
                    tryAddTo2(index)
                } else {
                    if (!isR()) {
                        refusedCount++
                    }
                }
            }
            StateP1.BLOCK -> {
                tryAddTo2(index)
            }
        }
    }
    println(
        "p001=${1f * p001 / n},\n" +
                "p201=${1f * p201 / n}, \n" +
                "p010=${1f * p010 / n},\n" +
                "p110=${1f * p110 / n},\n" +
                "p011=${1f * p011 / n},\n" +
                "p111=${1f * p111 / n},\n" +
                "p101=${1f * p101 / n},\n" +
                "\n\n\n" +
                "A=${1f * processedCount / n}\n" +
                "Pблок=${1f * blockCount / n}\n" +
                "Pотк=${1f * refusedCount / totalCount}\n" +
                "Q=${1f * processedCount / totalCount}\n" +
                "Lоч=${1f * queueCounter / n}\n" +
                "Lc=${1f * reqsNowCounter / n}\n" +
                "Wc=${1f * reqProcSystemCounter / processedCount}\n" + // можно посчитать как количество тактов на одну заявку
                "Wс(2)=${1f * reqsNowCounter / processedCount}\n" +// а можно как сколько количество заявок в одном такте. По сути одно и тоже.
                "Wоч=${1f * queueCounter / processedCount}\n"
    )
}