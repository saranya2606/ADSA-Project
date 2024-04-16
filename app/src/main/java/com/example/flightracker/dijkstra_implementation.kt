package com.example.flightracker


class Graph(private val numVertices: Int) {
    private val adjacencyList: MutableList<MutableList<Pair<Int, Int>>> = MutableList(numVertices) { mutableListOf() }

    fun addEdge(source: Int, destination: Int, weight: Int) {
        adjacencyList[source].add(Pair(destination, weight))
        adjacencyList[destination].add(Pair(source, weight)) // Assuming undirected graph
    }

    fun dijkstra(source: Int, destination: Int): Pair<List<Int>, Int> {
        val distance = MutableList(numVertices) { Int.MAX_VALUE }
        val previous = MutableList(numVertices) { -1 }
        val visited = BooleanArray(numVertices)

        distance[source] = 0

        val fibHeap = FibonacciHeap()
        fibHeap.insert(source, 0)

        while (!fibHeap.isEmpty()) {
            val minNode = fibHeap.extractMin() ?: break
            val u = minNode.vertex

            visited[u] = true

            for ((v, weight) in adjacencyList[u]) {
                if (!visited[v] && distance[u] != Int.MAX_VALUE && distance[u] + weight < distance[v]) {
                    distance[v] = distance[u] + weight
                    previous[v] = u
                    fibHeap.insert(v, distance[v])
                }
            }
        }

        val shortestPath = mutableListOf<Int>()
        var current = destination
        while (current != -1) {
            shortestPath.add(current)
            current = previous[current]
        }
        shortestPath.reverse()

        return Pair(shortestPath, distance[destination])
    }
}