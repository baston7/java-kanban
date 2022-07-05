package tests;

import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTests {
    InMemoryHistoryManager manager;
    @BeforeEach
    public void createManager(){
        manager=new InMemoryHistoryManager();
    }
    @Test
    public void addTasksAndGetHistoryTest(){
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", Status.NEW,1);
        Task task1 = new Task("Починить авто", "Съездить в сервис", Status.NEW,2);
        Task task2 = new Task("Сходить в кино", "Купить билеты в кино", Status.NEW,3);

        boolean isEmpty=manager.getHistory().isEmpty();
        assertTrue(isEmpty, "После вызова метода список истории должен быть пуст");
        manager.add(task);
        boolean isEmpty2=manager.getHistory().isEmpty();
        assertFalse(isEmpty2, "После вызова метода список истории не должен быть пуст");

        manager.add(task1);
        manager.add(task2);
        manager.add(task);
        manager.add(task1);

        int  size=manager.getHistory().size();
        Task task3=manager.getHistory().get(0);

        assertEquals(3,size,"Менеджер не удаляет дублирование просмотров" );
        assertEquals(task2,task3,"Менеджер неверно расставляет задачи" );
    }
    @Test
    public void removeTasksAndGetHistoryTest(){
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", Status.NEW,1);
        Task task1 = new Task("Починить авто", "Съездить в сервис", Status.NEW,2);
        Task task2 = new Task("Сходить в кино", "Купить билеты в кино", Status.NEW,3);

        boolean isEmpty=manager.getHistory().isEmpty();
        assertTrue(isEmpty, "После вызова метода список истории должен быть пуст");
        manager.remove(55);
        boolean isEmpty4=manager.getHistory().isEmpty();
        assertTrue(isEmpty4, "После вызова метода список истории должен быть пуст");
        manager.add(task);
        boolean isEmpty2=manager.getHistory().isEmpty();
        assertFalse(isEmpty2, "После вызова метода список истории не должен быть пуст");
        manager.remove(1);
        boolean isEmpty3=manager.getHistory().isEmpty();
        assertTrue(isEmpty3,"После вызова метода список истории должен быть пуст");

        manager.add(task);
        manager.add(task1);
        manager.add(task2);

        manager.remove(1);
        Task task3=manager.getHistory().get(0);
        assertEquals(task1,task3,"Менеджер неверно не удалил задачу из начала" );
        manager.add(task);
        manager.remove(1);
        Task task4=manager.getHistory().get(manager.getHistory().size()-1);
        assertEquals(task2,task4,"Менеджер неверно не удалил задачу из конца" );

        manager.add(task);
        manager.add(task1);
        manager.add(task2);

        manager.remove(2);

        Task task5=manager.getHistory().get(manager.getHistory().size()-1);
        Task task6=manager.getHistory().get(0);

        assertEquals(task,task6,"Менеджер неверно не удалил задачу из середины" );
        assertEquals(task2,task5,"Менеджер неверно не удалил задачу из середины" );
    }

}
