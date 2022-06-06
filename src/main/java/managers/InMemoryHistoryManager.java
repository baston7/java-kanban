package managers;

import customs.Node;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();// хэш мап для удаления повторных просмотров
    private Node head;
    private Node tail;

    public void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(task, null, oldTail);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        historyMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        final List<Task> historyList = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            historyList.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }
        return historyList;
    }


    private void removeNode(Node node) {
        if (node.getPrev() == null && node.getNext() == null) {
            head = null;
            tail = null;
        } else if (node.getPrev() == null) {
            head = head.getNext();
            head.setPrev(null);
        } else if (node.getNext() == null) {
            tail = tail.getPrev();
            tail.setNext(null);
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (!historyMap.containsKey(task.getId())) {
                linkLast(task);
            } else {
                removeNode(historyMap.get(task.getId()));
                linkLast(task);// обновляем узел в хэшмап
            }
        }
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        removeNode(node);
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
