// PROG2 VT2025, Inlämningsuppgift, del 1
// Grupp 65
// Isabelle Holloway & Alice Hellström

package se.su.ovning4;

import java.util.*;

public class ListGraph<T> implements Graph<T> {

    private final Map<T, List<se.su.inlupp.Edge<T>>> adjacencyList = new HashMap<>();

    @Override
    public void add(T node) {
        if (node == null)
            throw new IllegalArgumentException("Node cannot be null");
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("Vikten får inte vara negativ.");
        if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2))
            throw new NoSuchElementException("En eller båda noderna finns inte i grafen.");

        for (se.su.inlupp.Edge<T> edge : adjacencyList.get(node1)) {
            if (edge.getDestination().equals(node2.toString())) {
                throw new IllegalStateException("Det finns redan en förbindelse mellan dessa noder.");
            }
        }

        adjacencyList.get(node1).add(new se.su.inlupp.Edge<>(node2.toString(), name, weight));
        adjacencyList.get(node2).add(new se.su.inlupp.Edge<>(node1.toString(), name, weight));
    }

    @Override
    public void disconnect(T node1, T node2) {
        if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2))
            throw new NoSuchElementException("One or both nodes do not exist.");

        boolean removed1 = adjacencyList.get(node1).removeIf(e -> e.getDestination().equals(node2.toString()));
        boolean removed2 = adjacencyList.get(node2).removeIf(e -> e.getDestination().equals(node1.toString()));

        if (!removed1 || !removed2)
            throw new IllegalStateException("No connection exists between the nodes.");
    }

    @Override
    public void remove(T node) {
        if (!adjacencyList.containsKey(node))
            throw new NoSuchElementException("Node does not exist in the graph.");

        for (T other : adjacencyList.keySet()) {
            adjacencyList.get(other).removeIf(e -> e.getDestination().equals(node.toString()));
        }
        adjacencyList.remove(node);
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {
        if (weight < 0)
            throw new IllegalArgumentException("Vikten får inte vara negativ.");
        if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2))
            throw new NoSuchElementException("En eller båda noderna finns inte i grafen.");

        boolean updated = false;

        for (se.su.inlupp.Edge<T> edge : adjacencyList.get(node1)) {
            if (edge.getDestination().equals(node2.toString())) {
                edge.setWeight(weight);
                updated = true;
                break;
            }
        }

        for (se.su.inlupp.Edge<T> edge : adjacencyList.get(node2)) {
            if (edge.getDestination().equals(node1.toString())) {
                edge.setWeight(weight);
                updated = true;
                break;
            }
        }

        if (!updated)
            throw new NoSuchElementException("Ingen förbindelse finns mellan dessa noder.");
    }

    @Override
    public Set<T> getNodes() {
        return new HashSet<>(adjacencyList.keySet());
    }

    @Override
    public Collection<se.su.inlupp.Edge<T>> getEdgesFrom(T node) {
        if (!adjacencyList.containsKey(node))
            throw new NoSuchElementException("Noden finns ej i grafen");
        return new HashSet<>(adjacencyList.get(node));
    }

    @Override
    public se.su.inlupp.Edge<T> getEdgeBetween(T node1, T node2) {
        if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2))
            throw new NoSuchElementException("En eller båda noderna finns ej i grafen");

        for (se.su.inlupp.Edge<T> edge : adjacencyList.get(node1)) {
            if (edge.getDestination().equals(node2.toString())) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public boolean pathExists(T from, T to) {
        if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
            return false;
        }

        Set<T> visited = new HashSet<>();
        return dfs(from, to, visited);
    }

    private boolean dfs(T current, T target, Set<T> visited) {
        if (current.equals(target)) return true;
        visited.add(current);

        for (se.su.inlupp.Edge<T> edge : adjacencyList.get(current)) {
            T neighbor = findNode(edge.getDestination());
            if (neighbor != null && !visited.contains(neighbor)) {
                if (dfs(neighbor, target, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<se.su.inlupp.Edge<T>> getPath(T from, T to) {
        if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
            return null;
        }

        Set<T> visited = new HashSet<>();
        Map<T, T> predecessor = new HashMap<>();
        Map<T, se.su.inlupp.Edge<T>> edgeUsed = new HashMap<>();

        boolean found = dfsGetPath(from, to, visited, predecessor, edgeUsed);
        if (!found) return null;

        LinkedList<se.su.inlupp.Edge<T>> path = new LinkedList<>();
        T current = to;

        while (!current.equals(from)) {
            se.su.inlupp.Edge<T> edge = edgeUsed.get(current);
            path.addFirst(edge);
            current = predecessor.get(current);
        }

        return path;
    }

    private boolean dfsGetPath(T current, T target, Set<T> visited,
                               Map<T, T> predecessor, Map<T, se.su.inlupp.Edge<T>> edgeUsed) {
        visited.add(current);
        if (current.equals(target)) return true;

        for (se.su.inlupp.Edge<T> edge : adjacencyList.get(current)) {
            T neighbor = findNode(edge.getDestination());
            if (neighbor != null && !visited.contains(neighbor)) {
                predecessor.put(neighbor, current);
                edgeUsed.put(neighbor, edge);
                if (dfsGetPath(neighbor, target, visited, predecessor, edgeUsed)) {
                    return true;
                }
            }
        }
        return false;
    }

    private T findNode(String destination) {
        for (T node : adjacencyList.keySet()) {
            if (node.toString().equals(destination)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T node : adjacencyList.keySet()) {
            sb.append(node).append(": ");
            for (se.su.inlupp.Edge<T> edge : adjacencyList.get(node)) {
                sb.append(edge.toString()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
