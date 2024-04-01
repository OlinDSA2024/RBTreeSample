package org.example

/**
 * A self-balancing search tree.
 * Currently only supports insertion and lookup.
 * @param T a comparable type representing the keys to store
 */
class RBTree<T: Comparable<T>> {
    private var root: RBTreeNode<T>? = null

    /**
     * An internal class that represents a node in our RBTree
     * @param k the value to store
     * @param isBlack true if black and false otherwise
     */
    private class RBTreeNode<T: Comparable<T>>(val k: T,
                                               var isBlack: Boolean) {
        var parent: RBTreeNode<T>? = null
        var leftChild: RBTreeNode<T>? = null
        var rightChild: RBTreeNode<T>? = null

        /**
         * @return if this node is a left child of its parent
         */
        fun isLeftChild(): Boolean {
            return this == parent?.leftChild
        }

        /**
         * @return if this node is a right child of its parent
         */
        fun isRightChild(): Boolean {
            return this == parent?.rightChild
        }
    }

    /**
     * @return true if the tree contains the query
     */
    operator fun contains(q: T): Boolean {
        root?.let {
            return checkIn(it, q)
        } ?: run {
            return false
        }
    }

    /**
     * Search for the query [q] in [subtree]
     * @param subtree the subtree to search through
     * @param q the query to look for
     */
    private fun checkIn(subtree: RBTreeNode<T>, q: T):Boolean {
        if (subtree.k == q) {
            return true
        } else if (subtree.k > q) {
            subtree.leftChild?.let {
                return checkIn(it, q)
            } ?: run {
                return false
            }
        } else {
            subtree.rightChild?.let {
                return checkIn(it, q)
            } ?: run {
                return false
            }
        }
    }

    /**
     * Insert the [newValue] into the tree
     * @param newValue the value to insert
     */
    fun insert(newValue: T) {
        val new = RBTreeNode<T>(k = newValue, isBlack = false)
        root?.let {
            insertHelper(new, it)
        } ?: run {
            root = new
        }
        return insertFixup(new)
    }

    /**
     * A helper function to insert the node [new] into the subtree [curr]
     * @param new the node to insert
     * @param curr the subtree to insert into
     */
    private fun insertHelper(new: RBTreeNode<T>, curr: RBTreeNode<T>) {
        if (curr.k > new.k) {
            // move it down the left subtree
            curr.leftChild?.let { leftChild ->
                return insertHelper(new, leftChild)
            } ?: run {
                curr.leftChild = new
                new.parent = curr
            }
        } else {
            // move it down the right subtree
            curr.rightChild?.let { rightChild ->
                return insertHelper(new, rightChild)
            } ?: run {
                curr.rightChild = new
                new.parent = curr
            }
        }
    }

    private fun insertFixup(startWith: RBTreeNode<T>) {
        var z = startWith
        while (z.parent != null && !z.parent?.isBlack!!) {
            if (z.parent == z.parent?.parent?.leftChild) {
                val uncle = z.parent?.parent?.rightChild
                val uncleIsBlack = (uncle?.isBlack) ?: true
                if (!uncleIsBlack) {
                    z.parent?.isBlack = true
                    uncle?.isBlack = true
                    z.parent?.parent?.isBlack = false
                    z = z.parent?.parent!!
                } else {
                    if (z.isRightChild()) {
                        z = z.parent!!
                        leftRotate(z)
                    }
                    z.parent?.isBlack = true
                    z.parent?.parent?.isBlack = false
                    rightRotate(z.parent!!.parent!!)
                }
            } else {
                val uncle = z.parent?.parent?.leftChild
                val uncleIsBlack = uncle?.isBlack ?: true
                if (!uncleIsBlack) {
                    z.parent?.isBlack = true
                    uncle?.isBlack = true
                    z.parent?.parent?.isBlack = false
                    z = z.parent?.parent!!
                } else {
                    if (z.isLeftChild()) {
                        z = z.parent!!
                        rightRotate(z)
                    }
                    z.parent?.isBlack = true
                    z.parent?.parent?.isBlack = false
                    leftRotate(z.parent?.parent!!)
                }
            }
        }
        root?.isBlack = true
    }

    /**
     * Rotate the node [x] to the left
     * @param x the node to rotate
     */
    private fun leftRotate(x: RBTreeNode<T>) {
        val y = x.rightChild
        x.rightChild = y?.leftChild

        if (y?.leftChild != null) {
            y.leftChild?.parent = x
        }

        val xParent = x.parent       // this local variable helps with type safety
        y?.parent = xParent
        if (xParent == null) {
            root = y
        } else if (x == xParent.leftChild) {
            xParent.leftChild = y
        } else {
            xParent.rightChild = y
        }

        y?.leftChild = x
        x.parent = y
    }

    /**
     * Rotate the node [x] to the right
     * @param x the node to rotate
     */
    private fun rightRotate(x: RBTreeNode<T>) {
        val y = x.leftChild
        x.leftChild = y?.rightChild

        if (y?.rightChild != null) {
            y.rightChild?.parent = x
        }

        val xParent = x.parent       // this local variable helps with type safety
        y?.parent = xParent
        if (xParent == null) {
            root = y
        } else if (x == xParent.rightChild) {
            xParent.rightChild = y
        } else {
            xParent.leftChild = y
        }

        y?.rightChild = x
        x.parent = y
    }

    /**
     * Print a representation of the tree.
     */
    fun print() {
        printHelper(root, indent=0)
    }

    /**
     * Print the subtree rooted at [node] with the [indent] number of
     * spaces as indentation.
     * @param node the subtree to print
     * @param indent the number of spaces to use as indentation
     */
    private fun printHelper(node: RBTreeNode<T>?, indent: Int) {
        val indentString = " ".repeat(indent)
        if (node == null) {
            println("${indentString}null")
            return
        }
        println("${indentString}${if (node.isBlack) {"black" } else {"red"}} ${node.k}")
        println("${indentString}left")
        printHelper(node=node.leftChild, indent=indent+2)
        println("${indentString}right")
        printHelper(node=node.rightChild, indent=indent+2)
    }

    /**
     * Will throw an assertion exception if the invariants are *not *met
     */
    fun checkInvariants() {
        // property 1: nodes are either red or black (doesn't have to be chcked
        // property 2: the root and leaves are always black
        assert((root?.isBlack) ?: true)
        // don't have to check leaves as these are always represented by null
        // property 3
        assert(checkProperty3(root))
        assert(checkProperty4(root))
        assert(isBST(root))
    }

    /**
     * @return true if the binary search property is met
     */
    private fun isBST(n: RBTreeNode<T>?): Boolean {
        if (n != null) {
            val nLeftChild = n.leftChild
            val nRightChild = n.rightChild
            return ((nLeftChild == null || nLeftChild.k <= n.k) &&
                    (nRightChild == null || nRightChild.k > n.k))
        } else {
            return true
        }
    }

    /**
     * @return the height of the tree
     */
    fun getHeight(): Int {
        return getHeightHelper(root)
    }

    /**
     * @return the height of the subtree rooted at [n]
     */
    private fun getHeightHelper(n: RBTreeNode<T>?):Int {
        return if (n != null) {
            1 + maxOf(getHeightHelper(n.leftChild), getHeightHelper(n.rightChild))
        } else {
            0
        }
    }

    /**
     * Verifies property 3 which is that no red node can have a black child
     */
    private fun checkProperty3(n: RBTreeNode<T>?): Boolean {
        if (n != null) {
            val leftChildBlack = n.leftChild?.isBlack ?: true
            val rightChildBlack = n.leftChild?.isBlack ?: true

            if (!n.isBlack && (!leftChildBlack || !rightChildBlack)) {
                return false
            }
            return (checkProperty3(n.leftChild) && checkProperty3(n.rightChild))
        } else {
            return true
        }
    }

    /**
     * Verifies that the number of black nodes on the path from
     * the root to a leaf is always the same
     * @return true if the invariant is satisfied and false otherwise
     */
    private fun checkProperty4(n: RBTreeNode<T>?): Boolean {
        if (n != null) {
            val r = getProperty4(n.leftChild)
            val s = getProperty4(n.rightChild)
            return (r == s)
        } else {
            return true
        }
    }

    /**
     * Return the number of black nodes from the subtree rooted
     * at [n] to a leaf.
     * @param n the subtree to check
     * @return the number of black nodes from teh subtree to each leaf
     */
    private fun getProperty4(n: RBTreeNode<T>?): Int {
        if (n != null) {
            val r = getProperty4(n.leftChild)
            val s = getProperty4(n.rightChild)
            assert(r == s)
            return r + if (n.isBlack) { 1 } else { 0 }
        } else {
            return 1
        }
    }
}