package view;

import entity.Task;
import entity.Category;
import interface_adapter.TaskListController;
import interface_adapter.TaskListViewModel;
import interface_adapter.SetTimerController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Task list UI panel for the dashboard.
 * Uses TaskListController + TaskListViewModel + SetTimerController.
 */
public class TaskListView extends JPanel implements PropertyChangeListener {

    private final TaskListController taskController;
    private final TaskListViewModel viewModel;
    private final SetTimerController timerController;

    private final JPanel activeListPanel = new JPanel();
    private final JPanel completedListPanel = new JPanel();
    private final JLabel errorLabel = new JLabel();

    private Task draggingTask = null;
    private Task dragTargetTask = null;

    public TaskListView(TaskListController taskController,
                        TaskListViewModel viewModel,
                        SetTimerController timerController) {
        this.taskController = taskController;
        this.viewModel = viewModel;
        this.timerController = timerController;

        setLayout(new BorderLayout());
        initUI();

        this.viewModel.addPropertyChangeListener(this);

        this.taskController.onDashboardOpened();
    }

    // =====UI setup=====
    private void initUI() {
        // Top bar: title + Categories button
        JPanel topBar = new JPanel(new BorderLayout());

        JLabel title = new JLabel("My To-Do List");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        topBar.add(title, BorderLayout.WEST);

        JButton categoriesButton = new JButton("Categories...");
        categoriesButton.setToolTipText("Manage categories and their priorities");
        categoriesButton.addActionListener(e -> openCategoriesDialog());
        topBar.add(categoriesButton, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Center content with active + completed
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        activeListPanel.setLayout(new BoxLayout(activeListPanel, BoxLayout.Y_AXIS));
        JScrollPane activeScroll = new JScrollPane(activeListPanel);
        activeScroll.setBorder(BorderFactory.createTitledBorder("Active Tasks"));

        completedListPanel.setLayout(new BoxLayout(completedListPanel, BoxLayout.Y_AXIS));
        JScrollPane completedScroll = new JScrollPane(completedListPanel);
        completedScroll.setBorder(BorderFactory.createTitledBorder("Completed Tasks"));

        centerPanel.add(activeScroll);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(completedScroll);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom bar: New Task + error label
        JButton addButton = new JButton("New Task");
        addButton.addActionListener(e -> showNewTaskDialog());

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(addButton, BorderLayout.WEST);
        bottomBar.add(errorLabel, BorderLayout.CENTER);

        add(bottomBar, BorderLayout.SOUTH);
    }

    // =====PropertyChangeListener=====
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (TaskListViewModel.PROPERTY_ACTIVE.equals(prop)) {
            refreshActiveTasks();
        } else if (TaskListViewModel.PROPERTY_COMPLETED.equals(prop)) {
            refreshCompletedTasks();
        } else if (TaskListViewModel.PROPERTY_ERROR.equals(prop)) {
            updateErrorLabel();
        } else if (TaskListViewModel.PROPERTY_CATEGORIES.equals(prop)) {
            // Categories changed — nothing to redraw here directly,
            // but dialogs that read categories will see the updated list.
        }
    }

    // =====Refresh methods=====
    private void refreshActiveTasks() {
        activeListPanel.removeAll();

        List<Task> tasks = viewModel.getActiveTasks();
        if (tasks != null) {
            for (Task task : tasks) {
                activeListPanel.add(createActiveTaskRow(task));
            }
        }

        activeListPanel.revalidate();
        activeListPanel.repaint();
    }

    private void refreshCompletedTasks() {
        completedListPanel.removeAll();

        List<Task> tasks = viewModel.getCompletedTasks();
        if (tasks != null) {
            for (Task task : tasks) {
                completedListPanel.add(createCompletedTaskRow(task));
            }
        }

        completedListPanel.revalidate();
        completedListPanel.repaint();
    }

    private void updateErrorLabel() {
        String err = viewModel.getErrorMessage();
        if (err != null && !err.isEmpty()) {
            errorLabel.setText(err);
        } else {
            errorLabel.setText("");
        }
    }

    // =====Row creation=====
    private JComponent createActiveTaskRow(Task task) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        // checkbox for completion
        JCheckBox completeBox = new JCheckBox();
        completeBox.setSelected(task.isComplete());
        completeBox.addActionListener(e ->
                taskController.onToggleComplete(task.getId(), completeBox.isSelected())
        );

        // task name label with category name if not UNSORTED
        String labelText = task.getTaskName();
        if (task.getCategory() != null &&
                !"Unsorted".equals(task.getCategory().getName())) {
            labelText += " [" + task.getCategory().getName() + "]";
        }
        JLabel nameLabel = new JLabel(labelText);

        // timer button
        JButton timerButton = new JButton("⏱️");
        timerButton.setToolTipText("Set Timer");
        timerButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Set timer (minutes):",
                    "Set Timer for " + task.getTaskName(),
                    JOptionPane.PLAIN_MESSAGE
            );
            if (input != null && !input.isEmpty()) {
                try {
                    long mins = Long.parseLong(input.trim());
                    timerController.execute(task.getTaskName(), mins, 0);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter a valid number.",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });

        // details button
        JButton detailsButton = new JButton("Details");
        detailsButton.setToolTipText("Edit task details");
        detailsButton.addActionListener(e -> openDetailsDialog(task));

        row.add(completeBox);
        row.add(nameLabel);
        row.add(Box.createHorizontalStrut(8));
        row.add(timerButton);
        row.add(Box.createHorizontalStrut(4));
        row.add(detailsButton);

        // --- Quick & dirty drag-and-drop reordering on ACTIVE list ---

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    draggingTask = task;
                    dragTargetTask = task; // start with itself
                    row.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // While dragging, hovering over another row makes it the potential target
                if (draggingTask != null) {
                    dragTargetTask = task;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggingTask != null) {
                    row.setCursor(Cursor.getDefaultCursor());

                    List<Task> active = viewModel.getActiveTasks();
                    int fromIndex = indexOfTask(active, draggingTask);
                    int toIndex = indexOfTask(active, dragTargetTask);

                    if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                        // Call existing pin use case with new index
                        taskController.onPinTask(draggingTask.getId(), toIndex);
                    }

                    draggingTask = null;
                    dragTargetTask = null;
                }
            }
        });

        return row;
    }

    private JComponent createCompletedTaskRow(Task task) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        String labelText = task.getTaskName();
        if (task.getCategory() != null &&
                !"Unsorted".equals(task.getCategory().getName())) {
            labelText += " [" + task.getCategory().getName() + "]";
        }

        JLabel nameLabel = new JLabel(labelText);
        nameLabel.setForeground(Color.GRAY);

        // Restore button: move back to active (mark incomplete)
        JButton restoreButton = new JButton("Restore");
        restoreButton.setToolTipText("Move back to active tasks");
        restoreButton.addActionListener(e ->
                taskController.onToggleComplete(task.getId(), false)
        );

        // Delete button: remove forever
        JButton deleteButton = new JButton("Delete");
        deleteButton.setToolTipText("Delete this task permanently");
        deleteButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to permanently delete this task?\n\n" + task.getTaskName(),
                    "Delete Task",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                taskController.onDeleteTask(task.getId());
            }
        });

        // Optional: still allow editing details (category) if you want
        JButton detailsButton = new JButton("Details");
        detailsButton.setToolTipText("Edit task details");
        detailsButton.addActionListener(e -> openDetailsDialog(task));

        row.add(nameLabel);
        row.add(Box.createHorizontalStrut(4));
        row.add(restoreButton);
        row.add(Box.createHorizontalStrut(4));
        row.add(deleteButton);
        row.add(Box.createHorizontalStrut(4));
        row.add(detailsButton);

        return row;
    }


    // =====Dialogs=====

    // New Task pop-up (name only)
    private void showNewTaskDialog() {
        String name = JOptionPane.showInputDialog(
                this,
                "Enter task name:",
                "New Task",
                JOptionPane.PLAIN_MESSAGE
        );
        if (name != null) {
            name = name.trim();
            if (!name.isEmpty()) {
                taskController.onAddTask(name);
            }
        }
    }

    private void openDetailsDialog(Task task) {
        // Refresh category list from use case
        taskController.onViewCategories();

        Window parent = SwingUtilities.getWindowAncestor(this);
        final JDialog dialog = new JDialog(parent, "Task details", Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ----- Category row -----
        gbc.gridx = 0;
        gbc.gridy = row;
        dialog.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        List<Category> categories = viewModel.getCategories();
        if (categories == null || categories.isEmpty()) {
            categories = new java.util.ArrayList<Category>();
            categories.add(Category.UNSORTED);
        }

        JComboBox<Category> categoryCombo =
                new JComboBox<Category>(categories.toArray(new Category[0]));

        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    label.setText(((Category) value).getName());
                }
                return label;
            }
        });

        // Select the current category
        Category current = task.getCategory();
        if (current != null) {
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                if (categoryCombo.getItemAt(i).getName().equalsIgnoreCase(current.getName())) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Instant apply category changes
        categoryCombo.addActionListener(e -> {
            Category selected = (Category) categoryCombo.getSelectedItem();
            String name = (selected != null) ? selected.getName() : null;
            taskController.onChangeCategory(task.getId(), name);
        });

        dialog.add(categoryCombo, gbc);
        row++;

        // ----- Reset Position button -----
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JButton resetButton = new JButton("Reset Position");
        resetButton.addActionListener(e -> {
            taskController.onPinTask(task.getId(), -1);
            JOptionPane.showMessageDialog(dialog,
                    "Task returned to normal sorting order.",
                    "Reset Position",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        dialog.add(resetButton, gbc);
        gbc.gridwidth = 1;
        row++;

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        bottomPanel.add(closeButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        dialog.add(bottomPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Manage Categories dialog
    private void openCategoriesDialog() {
        taskController.onViewCategories();

        Window parent = SwingUtilities.getWindowAncestor(this);
        final JDialog dialog = new JDialog(parent, "Manage Categories", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(8, 8));

        // Left: list of categories
        DefaultListModel<Category> listModel = new DefaultListModel<Category>();
        JList<Category> categoryList = new JList<Category>(listModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        categoryList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    Category c = (Category) value;
                    String text = c.getName() + " (priority " + c.getPriority() + ")";
                    label.setText(text);
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(categoryList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Categories"));
        dialog.add(scrollPane, BorderLayout.CENTER);

        Runnable reloadCategories = new Runnable() {
            @Override
            public void run() {
                listModel.clear();
                List<Category> cats = viewModel.getCategories();
                if (cats != null) {
                    for (Category c : cats) {
                        listModel.addElement(c);
                    }
                }
            }
        };
        reloadCategories.run();

        // Right: form to add/update category
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(12);
        formPanel.add(nameField, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Priority:"), gbc);

        gbc.gridx = 1;
        JSpinner prioritySpinner = new JSpinner(
                new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1)
        );
        formPanel.add(prioritySpinner, gbc);

        row++;

        JButton addOrUpdateButton = new JButton("Add / Update");
        JButton deleteButton = new JButton("Delete");

        JPanel buttonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsRight.add(addOrUpdateButton);
        buttonsRight.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonsRight, gbc);

        dialog.add(formPanel, BorderLayout.EAST);

        // selection in list loads into form
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Category selected = categoryList.getSelectedValue();
                if (selected != null) {
                    nameField.setText(selected.getName());
                    prioritySpinner.setValue(selected.getPriority());
                }
            }
        });

        // add/update button
        addOrUpdateButton.addActionListener(e -> {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Category name cannot be empty.",
                        "Invalid Category",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int priority = ((Integer) prioritySpinner.getValue()).intValue();

            taskController.onAddOrUpdateCategory(name.trim(), priority);
            taskController.onViewCategories();
            reloadCategories.run();
        });

        // delete button
        deleteButton.addActionListener(e -> {
            Category selected = categoryList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select a category to delete.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            taskController.onDeleteCategory(selected.getName());
            taskController.onViewCategories();
            reloadCategories.run();

            nameField.setText("");
            prioritySpinner.setValue(0);
        });

        // close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(closeButton);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // helper for finding the index of a task
    private int indexOfTask(List<Task> list, Task t) {
        if (list == null || t == null) return -1;
        for (int i = 0; i < list.size(); i++) {
            Task candidate = list.get(i);
            if (candidate != null && t.getId().equals(candidate.getId())) {
                return i;
            }
        }
        return -1;
    }
}
