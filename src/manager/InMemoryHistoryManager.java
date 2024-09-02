package manager;

import model.Task;
import util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> taskPositionsInNodeList;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        head = null;
        tail = null;
        taskPositionsInNodeList = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        task = task.getCopy();
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (taskPositionsInNodeList.containsKey(id)) {
            Node<Task> node = taskPositionsInNodeList.get(id);
            removeNode(node);
            taskPositionsInNodeList.remove(id);
        }
    }

    private void linkLast(Task task) {
        Node<Task> node = new Node<>(task);
        taskPositionsInNodeList.put(task.getId(), node);

        // if list is empty:
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }

        tail = node;
    }

    private void removeNode(Node<Task> node) {
        // if list is empty:
        if (head == null) {
            return;
        }

        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        // if prev is exists => node is not the head, else: node is the head
        if (prev != null) {
            prev.next = next;
        } else {
            head = next;

            if (head != null) {
                head.prev = null;
            }
        }

        // if next is exists => node is not the tail, else: node is the tail
        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;

            if (tail != null) {
                tail.next = null;
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node<Task> node = head;

        while (node != null) {
            result.add(node.data);
            node = node.next;
        }

        return result;
    }
}
