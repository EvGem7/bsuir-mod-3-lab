import java.util.*

val n = 1_000_000_00
val r = 0.5
val p1 = 0.6
val p2 = 0.4

val random = Random()

fun isR(): Boolean = random.nextDouble() <= r
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

    var stateP1 = StateP1.IDLE
    var queueSlots = 1
    var isP2Proc = false

    fun tryGetNew() {
        if (!isR()) {
            stateP1 = StateP1.PROC
        }
    }

    fun tryAddTo2() {
        if (queueSlots == 1) {
            if (isP2Proc) {
                queueSlots--
            } else {
                isP2Proc = true
            }
            stateP1 = StateP1.IDLE
            tryGetNew()
        } else {
            stateP1 = StateP1.BLOCK
        }
    }

    repeat(n) {
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

        if (queueSlots < 1) {
            if (isP2Proc) {
                if (!isP2()) {
                    queueSlots++
                }
            } else {
                isP2Proc = true
                queueSlots++
            }
        } else {
            if (isP2Proc && !isP2()) {
                isP2Proc = false
            }
        }

        when (stateP1) {
            StateP1.IDLE -> {
                tryGetNew()
            }
            StateP1.PROC -> {
                if (!isP1()) {
                    tryAddTo2()
                }
            }
            StateP1.BLOCK -> {
                tryAddTo2()
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
                "p101=${1f * p101 / n},\n"
    )
}

//when (stateP1) {
//    StateP1.IDLE -> {
//        if (!isR()) {
//            stateP1 = StateP1.PROC
//        }
//    }
//    StateP1.PROC -> {
//        if (!isP1()) {
//            if (queueSlots > 0) {
//                queueSlots--
//            } else {
//                stateP1 = StateP1.BLOCK
//            }
//        }
//    }
//    StateP1.BLOCK -> if (queueSlots > 0) {
//        queueSlots--
//    }
//}
//if (isP2Proc && !isP2()) {
//    isP2Proc = false
//}
//if (!isP2Proc && queueSlots < 1) {
//    isP2Proc = true
//    queueSlots++
//}