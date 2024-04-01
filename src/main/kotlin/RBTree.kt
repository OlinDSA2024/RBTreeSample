package org.example

class RBTree {
    private var root: RBTreeNode? = null
    private class RBTreeNode(val k: Int,
                             var isBlack: Boolean) {
        var parent: RBTreeNode? = null
        var leftChild: RBTreeNode? = null
        var rightChild: RBTreeNode? = null
    }

    /**
     * Insert the [newValue] into the tree
     * @param newValue the value to insert
     */
    fun insert(newValue: Int) {
        val new = RBTreeNode(k = newValue, isBlack = false)
        val r = root   // local variable helps with type safety
        if (r == null) {
            root = new
        } else {
            insertHelper(new, r)
        }
        return insertFixup(new)
    }

    /**
     * A helper function to insert the node [new] into the subtree [curr]
     * @param new the node to insert
     * @param curr the subtree to insert into
     */
    private fun insertHelper(new: RBTreeNode, curr: RBTreeNode) {
        if (curr.k > new.k) {
            // move it down the left subtree
            val leftChild = curr.leftChild
            if (leftChild != null) {
                return insertHelper(new, leftChild)
            } else {
                curr.leftChild = new
                new.parent = curr
            }
        } else {
            val rightChild = curr.rightChild
            if (rightChild != null) {
                return insertHelper(new, rightChild)
            } else {
                curr.rightChild = new
                new.parent = curr
            }
        }
    }

    private fun insertFixup(startWith: RBTreeNode) {
        var z = startWith
        while (z.parent != null && !z.parent?.isBlack!!) {
            if (z.parent == z.parent?.parent?.leftChild) {
                val y = z.parent?.parent?.rightChild
                val yIsBlack = (y?.isBlack) ?: true
                if (!yIsBlack) {
                    z.parent?.isBlack = true
                    y?.isBlack = true
                    z.parent?.parent?.isBlack = false
                    z = z.parent?.parent!!
                } else {
                    if (z == z.parent?.rightChild) {
                        z = z.parent!!
                        leftRotate(z)
                    }
                    z.parent?.isBlack = true
                    z.parent?.parent?.isBlack = false
                    rightRotate(z.parent!!.parent!!)
                }
            } else {
                val y = z.parent?.parent?.leftChild
                val yIsBlack = y?.isBlack ?: true
                if (!yIsBlack) {
                    z.parent?.isBlack = true
                    y?.isBlack = true
                    z.parent?.parent?.isBlack = false
                    z = z.parent?.parent!!
                } else {
                    if (z == z.parent?.leftChild) {
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

    private fun leftRotate(x: RBTreeNode) {
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

    private fun rightRotate(x: RBTreeNode) {
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
    private fun printHelper(node: RBTreeNode?, indent: Int) {
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
    private fun isBST(n: RBTreeNode?): Boolean {
        if (n != null) {
            return (n.k >= (n.leftChild?.k ?: Int.MIN_VALUE) ||
                    n.k <= (n.rightChild?.k ?: Int.MAX_VALUE))
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
    private fun getHeightHelper(n: RBTreeNode?):Int {
        return if (n != null) {
            1 + maxOf(getHeightHelper(n.leftChild), getHeightHelper(n.rightChild))
        } else {
            0
        }
    }

    /**
     * Verifies property 3 which is that no red node can have a black child
     */
    private fun checkProperty3(n: RBTreeNode?): Boolean {
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

    private fun checkProperty4(n: RBTreeNode?): Boolean {
        if (n != null) {
            val r = getProperty4(n.leftChild)
            val s = getProperty4(n.rightChild)
            return (r == s)
        } else {
            return true
        }
    }

    private fun getProperty4(n: RBTreeNode?): Int {
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