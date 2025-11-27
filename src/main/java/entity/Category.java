package entity;

public class Category {

    private final String name;
    private final int priority;

    public static final int LOWEST_PRIORITY = Integer.MIN_VALUE;
    public static final Category UNSORTED =
            new Category("Unsorted", LOWEST_PRIORITY);

    public Category(String name, int priority) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be null or blank");
        }
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public int comparePriorityTo(Category other) {
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", priority=" + priority +
                '}';
    }
}
