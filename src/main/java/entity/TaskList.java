package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskList {
    private final List<Task> tasks;
    private final List<String> settings;

    public TaskList(List<Task> tasks, List<String> settings) {
        this.tasks = tasks;
        this.settings = settings;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean removeTaskById(String id) {
        return tasks.removeIf(t -> t.getId().equals(id));
    }

    public Task getTaskById(String id) {
        for (Task t : tasks) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    /**
     * Returns tasks sorted by:
     *  1. pinned index (priorityOverride) if !=-1
     *  2. otherwise by category priority
     *  3. then due date (earliest first)
     *  4. then alphabetical
     */
    public List<Task> getTasksSorted() {
        List<Task> pinned = new ArrayList<>();
        List<Task> normal = new ArrayList<>();

        for (Task t : tasks) {
            if (t.getPriorityOverride() >= 0) {
                pinned.add(t);
            } else {
                normal.add(t);
            }
        }

        normal.sort(defaultComparator());

        List<Task> result = new ArrayList<>(normal);

        pinned.sort(Comparator.comparingInt(Task::getPriorityOverride));

        for (Task t : pinned) {
            int index = t.getPriorityOverride();

            if (index < 0) {
                result.add(t);
                continue;
            }

            if (index >= result.size()) {
                result.add(t);
            } else {
                result.add(index, t);
            }
        }

        return result;
    }

    /**
     * Comparator for handling sorting rules mentioned above
     */
    private Comparator<Task> defaultComparator() {
        return (t1, t2) -> {
            // 1) Category priority
            int p1 = t1.getCategory().getPriority();
            int p2 = t2.getCategory().getPriority();
            int cmp = Integer.compare(p2, p1); // descending
            if (cmp != 0) return cmp;

            // 2) Due date: earlier first, no due date last
            LocalDateTime d1 = t1.getDueDate();
            LocalDateTime d2 = t2.getDueDate();

            if (d1 == null && d2 != null) return 1;   // nulls last
            if (d1 != null && d2 == null) return -1;
            if (d1 != null && d2 != null) {
                cmp = d1.compareTo(d2);
                if (cmp != 0) return cmp;
            }
            // If both null, or equal date, sort by alphabetical

            // 3) Alphabetical by task name
            String n1 = t1.getTaskName();
            String n2 = t2.getTaskName();
            if (n1 == null && n2 == null) return 0;
            if (n1 == null) return 1;
            if (n2 == null) return -1;

            return n1.compareToIgnoreCase(n2);
        };
    }
}
