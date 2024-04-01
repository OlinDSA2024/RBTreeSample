package org.example
import java.util.Random

fun main() {
    val tree = RBTree<Int>()
    val random = Random()
    val asList: MutableList<Int> = mutableListOf()

    for (i in 0 until 1000000) {
        val v = random.nextInt(100000)
        tree.insert(v)
        asList.add(v)
    }
    tree.checkInvariants()
    println("height: ${tree.getHeight()}")
    for (v in asList) {
        assert(v in tree)
    }

    // do a worst-case unit test
    val t2 = RBTree<Int>()
    for (i in 0 until 1000000) {
        t2.insert(i)
    }
    println("height: ${t2.getHeight()}")
    t2.checkInvariants()
}