package org.example
import java.util.Random

fun main() {
    val tree = RBTree()
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

    val t2 = RBTree()
    t2.insert(2)
    t2.insert(3)
    t2.insert(-1)
    t2.print()
}