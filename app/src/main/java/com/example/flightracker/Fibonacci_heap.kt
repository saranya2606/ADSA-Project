package com.example.flightracker

data class FibonacciNode(
    val vertex: Int,
    var distance: Int,
    var parent: FibonacciNode? = null,
    var child: FibonacciNode? = null,
    var left: FibonacciNode? = null,
    var right: FibonacciNode? = null,
    var degree: Int = 0,
    var marked: Boolean = false
)

class FibonacciHeap {
    var minNode: FibonacciNode? = null

    fun insert(vertex: Int, distance: Int) {
        val newNode = FibonacciNode(vertex, distance)
        if (minNode == null) {
            minNode = newNode
        } else {
            newNode.right = minNode
            newNode.left = minNode!!.left
            minNode!!.left?.right = newNode
            minNode!!.left = newNode
            if (distance < minNode!!.distance)
                minNode = newNode
        }
    }

    fun extractMin(): FibonacciNode? {
        val min = minNode ?: return null
        if (min == min.right) {
            minNode = null
        } else {
            min.left?.right = min.right
            min.right?.left = min.left
            minNode = min.right
            consolidate()
        }
        min.left = null
        min.right = null
        return min
    }

    fun decreaseKey(node: FibonacciNode, newDistance: Int) {
        node.distance = newDistance
        var x = node
        var y = x.parent
        if (y != null && x.distance < y.distance) {
            cut(x, y)
            cascadingCut(y)
        }
        if (x.distance < minNode!!.distance)
            minNode = x
    }
    fun isEmpty(): Boolean {
        return minNode == null
    }

    private fun consolidate() {
        val A = mutableListOf<FibonacciNode?>()
        for (i in 0 until 64) {
            A.add(null)
        }
        var w = minNode
        while (w != null) {
            var x = w
            var d = x.degree
            while (A[d] != null) {
                val y = A[d]!!
                if (x != null) {
                    if (x.distance > y.distance) {
                        val temp = x
                        x = y
                        y.right?.left = x
                        y.left?.right = x
                        x.left?.right = y
                        x.right?.left = y
                        x.left = y.left
                        x.right = y.right
                        x.parent = y.parent
                        x.child = y.child
                        if (temp != null) {
                            y.left = temp.left
                        }
                        if (temp != null) {
                            y.right = temp.right
                        }
                        if (temp != null) {
                            y.parent = temp.parent
                        }
                        if (temp != null) {
                            y.child = temp.child
                        }
                        if (x.parent != null && x.parent!!.child == temp)
                            x.parent!!.child = x
                        if (x.child != null)
                            x.child!!.parent = x
                    }
                }
                if (minNode == y)
                    minNode = x
                if (x != null) {
                    link(y, x)
                }
                A[d] = null
                d++
            }
            A[d] = x
            w = w.right
        }
    }

    private fun link(y: FibonacciNode, x: FibonacciNode) {
        y.right = x.child
        y.left = x.child?.left
        x.child?.left?.right = y
        x.child?.left = y
        y.parent = x
        x.degree++
        y.marked = false
    }

    private fun cut(x: FibonacciNode, y: FibonacciNode) {
        x.left?.right = x.right
        x.right?.left = x.left
        y.degree--
        if (y.child == x)
            y.child = x.right
        if (y.degree == 0)
            y.child = null
        x.parent = null
        x.marked = false
        minNode?.left?.right = x
        x.left = minNode?.left
        x.right = minNode
        minNode?.left = x
    }

    private fun cascadingCut(y: FibonacciNode) {
        val z = y.parent
        if (z != null) {
            if (!y.marked)
                y.marked = true
            else {
                cut(y, z)
                cascadingCut(z)
            }
        }
    }
}