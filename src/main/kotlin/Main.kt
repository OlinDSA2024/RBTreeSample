package org.example
import java.util.Random

fun main() {
    val tree = RBTree()
    val random = Random()

    for (i in 0 until 1000000) {
        tree.insert(random.nextInt(100000))
    }
    tree.checkInvariants()
    println("height: ${tree.getHeight()}")

    val t2 = RBTree()
    t2.insert(2)
    t2.insert(3)
    t2.insert(-1)
    t2.print()
}