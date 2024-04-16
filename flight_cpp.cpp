#include <bits/stdc++.h>
#include <vector>
#include <limits>
#include <unordered_set>
#include <unordered_map>

using namespace std;

// Define a structure to represent edges in the graph
struct Edge {
    int destination;
    int distance; // Distance in kilometers
    int duration; // Duration in minutes
    int fuelConsumption; // Fuel consumption in liters
    // You can add more attributes as needed, such as cost, departure/arrival times, etc.
};

// Define a structure to represent vertices in the graph
struct Vertex {
    vector<Edge> edges;
};

// Node structure for Fibonacci Heap
struct FibonacciNode {
    int vertex;
    int distance;
    bool marked;
    FibonacciNode* parent;
    FibonacciNode* child;
    FibonacciNode* left;
    FibonacciNode* right;
    int degree;
};

// Fibonacci Heap implementation
class FibonacciHeap {
private:
    FibonacciNode* minNode;
    unordered_map<int, FibonacciNode*> nodeMap;

public:
    FibonacciHeap() : minNode(nullptr) {}

    ~FibonacciHeap() {
        if (minNode != nullptr)
            destroyTree(minNode);
    }

  void insert(int vertex, int distance) {
    FibonacciNode* newNode = new FibonacciNode{vertex, distance, false, nullptr, nullptr, nullptr, nullptr, 0};
    if (minNode == nullptr) {
        minNode = newNode;
        minNode->left = newNode;
        minNode->right = newNode;
    } else {
        newNode->left = minNode->left;
        newNode->right = minNode;
        minNode->left->right = newNode;
        minNode->left = newNode;
        if (distance < minNode->distance)
            minNode = newNode;
    }
    nodeMap[vertex] = newNode;
}

    FibonacciNode* getMinNode() const {
        return minNode;
    }

    FibonacciNode* extractMin() {
        FibonacciNode* z = minNode;
        if (z != nullptr) {
            if (z->child != nullptr) {
                FibonacciNode* child = z->child;
                FibonacciNode* temp = child;
                do {
                    temp->parent = nullptr;
                    temp = temp->right;
                } while (temp != child);
                concatenateLists(minNode, child);
            }
            removeNode(z);
            if (z == z->right)
                minNode = nullptr;
            else {
                minNode = z->right;
                consolidate();
            }
        }
        return z;
    }

    void decreaseKey(FibonacciNode* x, int distance) {
        x->distance = distance;
        FibonacciNode* y = x->parent;
        if (y != nullptr && x->distance < y->distance) {
            cut(x, y);
            cascadingCut(y);
        }
        if (x->distance < minNode->distance)
            minNode = x;
    }

    bool isEmpty() const {
        return minNode == nullptr;
    }

private:
    void destroyTree(FibonacciNode* node) {
        if (node != nullptr) {
            FibonacciNode* current = node;
            do {
                FibonacciNode* next = current->right;
                destroyTree(current->child);
                delete current;
                current = next;
            } while (current != node);
        }
    }

    void concatenateLists(FibonacciNode* x, FibonacciNode* y) {
        FibonacciNode* temp = x->right;
        x->right = y->right;
        y->right->left = x;
        y->right = temp;
        temp->left = y;
    }

    void removeNode(FibonacciNode* x) {
    if (x == nullptr) {
        // Handle the case when x is null
        return;
    }
    if (x->left != nullptr && x->right != nullptr) {
        // Adjust pointers only if both left and right pointers are valid
        x->left->right = x->right;
        x->right->left = x->left;
    }
    // Remove the node from the nodeMap
    nodeMap.erase(x->vertex);
}


    void link(FibonacciNode* y, FibonacciNode* x) {
        removeNode(y);
        if (x->child == nullptr) {
            x->child = y;
            y->left = y;
            y->right = y;
        } else {
            y->left = x->child;
            y->right = x->child->right;
            x->child->right->left = y;
            x->child->right = y;
        }
        y->parent = x;
        x->degree++;
        y->marked = false;
    }

    void consolidate() {
    if (minNode == nullptr) {
        // Handle the case when the heap is empty
        return;
    }

    vector<FibonacciNode*> A(64, nullptr); // Assuming n <= 2^64
    FibonacciNode* w = minNode;
    do {
        FibonacciNode* x = w;
        FibonacciNode* nextW = w->right;
        if (nextW == nullptr) {
            // Handle the case when reaching the end of the circular list
            break;
        }
        int d = x->degree;
        while (A[d] != nullptr) {
            FibonacciNode* y = A[d];
            if (x->distance > y->distance)
                swap(x, y);
            link(y, x);
            A[d] = nullptr;
            d++;
        }
        A[d] = x;
        w = nextW;
    } while (w != minNode);


        minNode = nullptr;
        for (FibonacciNode* node : A) {
            if (node != nullptr) {
                if (minNode == nullptr)
                    minNode = node;
                else {
                    node->left = minNode->left;
                    node->right = minNode;
                    minNode->left->right = node;
                    minNode->left = node;
                    if (node->distance < minNode->distance)
                        minNode = node;
                }
            }
        }
    }

    void cut(FibonacciNode* x, FibonacciNode* y) {
        if (x == x->right)
            y->child = nullptr;
        else {
            x->left->right = x->right;
            x->right->left = x->left;
            if (y->child == x)
                y->child = x->right;
        }
        y->degree--;
        concatenateLists(minNode, x);
        x->parent = nullptr;
        x->marked = false;
    }

    void cascadingCut(FibonacciNode* y) {
        FibonacciNode* z = y->parent;
        if (z != nullptr) {
            if (!y->marked)
                y->marked = true;
            else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }
};

// Function to find the shortest path using Dijkstra's algorithm with Fibonacci Heap
vector<pair<int, int>> dijkstra(const vector<Vertex>& graph, int source, int destination, const unordered_set<int>& bannedAirports = {}) {
    int n = graph.size();
    vector<pair<int, int>> distanceAndPrev(n, {numeric_limits<int>::max(), -1}); // {distance, previous vertex}
    distanceAndPrev[source] = {0, -1}; // Distance from source to itself is 0

    FibonacciHeap fibHeap;
    fibHeap.insert(source, 0);

    while (!fibHeap.isEmpty()) {
        FibonacciNode* minNode = fibHeap.extractMin();
        int u = minNode->vertex;

        // Stop the search if the destination is reached
        if (u == destination)
            break;

        // Iterate over all adjacent vertices of u
        for (const Edge& edge : graph[u].edges) {
            int v = edge.destination;

            // Skip banned airports
            if (bannedAirports.find(v) != bannedAirports.end())
                continue;

            int distToV = distanceAndPrev[u].first + edge.distance;

            // Relaxation step: Update distance if a shorter path is found
            if (distToV < distanceAndPrev[v].first) {
                distanceAndPrev[v] = {distToV, u}; // Update distance and previous vertex
                fibHeap.insert(v, distToV);
            }
        }
    }

    return distanceAndPrev;
}

// Function to reconstruct the shortest path from source to destination
vector<int> reconstructPath(const vector<pair<int, int>>& distanceAndPrev, int destination) {
    vector<int> path;
    for (int v = destination; v != -1; v = distanceAndPrev[v].second)
        path.push_back(v);
    reverse(path.begin(), path.end());
    return path;
}

int main() {
vector<Vertex> graph = {
    {{ {1, 10, 5, 2}, {2, 15, 8, 3} }},    // Airport 0
    {{ {3, 20, 10, 4} }},                   // Airport 1
    {{ {3, 10, 5, 2}, {4, 15, 8, 3} }},     // Airport 2
    {{ {5, 20, 10, 4} }},                   // Airport 3
    {{ {5, 10, 5, 2} }},                    // Airport 4
    {{}}                                    // Destination airport
};

// Example banned airports (for demonstration purposes)
unordered_set<int> bannedAirports = {1}; // Airport 1 is banned

    int source = 0; // Source airport
    int destination = 5; // Destination airport
    vector<pair<int, int>> distanceAndPrev = dijkstra(graph, source, destination, bannedAirports);
    vector<int> shortestPath = reconstructPath(distanceAndPrev, destination);

    // Output shortest path
    cout << "Shortest path from airport " << source << " to airport " << destination << ":\n";
    for (int i = 0; i < shortestPath.size(); ++i) {
        cout << shortestPath[i];
        if (i != shortestPath.size() - 1)
            cout << " -> ";
    }
    cout << "\n";

    // Output total distance and other details of the shortest path
    cout << "Total distance: " << distanceAndPrev[destination].first << " km\n";
    // You can output additional details like duration, fuel consumption, etc.

    return 0;
}