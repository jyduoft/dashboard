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

    /**
     * Returns tasks sorted by:
     *  1. pinned index (priorityOverride) if !=-1
     *  2. otherwise by category priority
     *  3. then due date (earlier first)
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

        // 2. Sort normal tasks: category priority desc, then due date asc
        normal.sort(defaultComparator());

        // 3. Build result list:
        //    start with normal-ordered list, then insert pinned at requested indices
        List<Task> result = new ArrayList<>(normal);

        // To make behavior predictable, sort pinned by their override index
        pinned.sort(Comparator.comparingInt(Task::getPriorityOverride));

        for (Task t : pinned) {
            int index = t.getPriorityOverride();

            if (index < 0) {
                // just in case, treat as normal
                result.add(t);
                continue;
            }

            if (index >= result.size()) {
                // cannot place at requested index; "if possible" → append to end
                result.add(t);
            } else {
                // insert at requested position, shifting others to the right
                result.add(index, t);
            }
        }

        return result;
    }

    private Comparator<Task> defaultComparator() {
        return (t1, t2) -> {
            // 1) category priority: higher first
            int p1 = t1.getCategory().getPriority();
            int p2 = t2.getCategory().getPriority();
            int cmp = Integer.compare(p2, p1);
            if (cmp != 0) return cmp;

            // 2) due date: earlier first, nulls last
            LocalDateTime d1 = t1.getDueDate();
            LocalDateTime d2 = t2.getDueDate();

            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;

            return d1.compareTo(d2);
        };
    }
}
