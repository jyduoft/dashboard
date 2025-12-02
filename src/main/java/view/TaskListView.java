package view;

import entity.Task;
import interface_adapter.TaskListController;
import interface_adapter.TaskListViewModel;
import interface_adapter.SetTimerController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class TaskListView extends JPanel implements PropertyChangeListener {

    private final TaskListController taskController;
    private final TaskListViewModel viewModel;
    private final SetTimerController timerController;

    private final JPanel activeListPanel = new JPanel();
    private final JPanel completedListPanel = new JPanel();
    private final JLabel errorLabel = new JLabel();

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

    private void initUI() {
        JLabel title = new JLabel("My To-Do List");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

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

        JButton addButton = new JButton("New Task");
        addButton.addActionListener(e -> showNewTaskDialog());

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(addButton, BorderLayout.WEST);
        bottomBar.add(errorLabel, BorderLayout.CENTER);

        add(bottomBar, BorderLayout.SOUTH);
    }

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (TaskListViewModel.PROPERTY_ACTIVE.equals(prop)) {
            refreshActiveTasks();
        } else if (TaskListViewModel.PROPERTY_COMPLETED.equals(prop)) {
            refreshCompletedTasks();
        } else if (TaskListViewModel.PROPERTY_ERROR.equals(prop)) {
            updateErrorLabel();
        }
    }

    private void refreshActiveTasks() {
        activeListPanel.removeAll();

        if (viewModel.getActiveTasks() != null) {
            for (Task task : viewModel.getActiveTasks()) {
                activeListPanel.add(createActiveTaskRow(task));
            }
        }

        activeListPanel.revalidate();
        activeListPanel.repaint();
    }

    private void refreshCompletedTasks() {
        completedListPanel.removeAll();

        if (viewModel.getCompletedTasks() != null) {
            for (Task task : viewModel.getCompletedTasks()) {
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

    private void openDetailsDialog(Task task) {
        java.awt.Window parent = SwingUtilities.getWindowAncestor(this);
        final JDialog dialog = new JDialog(parent, "Task details", Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Category
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        JTextField categoryField = new JTextField(15);
        if (task.getCategory() != null) {
            categoryField.setText(task.getCategory().getName());
        }
        dialog.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Pin index (-1 for none):"), gbc);

        gbc.gridx = 1;
        JSpinner pinSpinner = new JSpinner(
                new SpinnerNumberModel(task.getPriorityOverride(), -1, Integer.MAX_VALUE, 1)
        );
        dialog.add(pinSpinner, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(buttonsPanel, gbc);

        saveButton.addActionListener(e -> {
            String categoryName = categoryField.getText();
            int pinIndex = (Integer) pinSpinner.getValue();

            // Use your use cases via controller:
            taskController.onChangeCategory(task.getId(), categoryName);
            taskController.onPinTask(task.getId(), pinIndex);

            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JComponent createActiveTaskRow(Task task) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JCheckBox completeBox = new JCheckBox();
        completeBox.setSelected(task.isComplete());
        completeBox.addActionListener(e ->
                taskController.onToggleComplete(task.getId(), completeBox.isSelected())
        );

        String labelText = task.getTaskName();
        if (task.getCategory() != null &&
                !"Unsorted".equals(task.getCategory().getName())) {
            labelText += " [" + task.getCategory().getName() + "]";
        }
        JLabel nameLabel = new JLabel(labelText);

        JButton detailsButton = new JButton("Details");
        detailsButton.setToolTipText("Edit task details");
        detailsButton.addActionListener(e -> openDetailsDialog(task));

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
                    timerController.execute(task.getId(), mins, 0);
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

        row.add(completeBox);
        row.add(nameLabel);
        row.add(Box.createHorizontalStrut(8));
        row.add(timerButton);
        row.add(detailsButton);

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

        row.add(nameLabel);
        return row;
    }

    /**
     * FOR TESTING ONLY. IGNORE!!! Generated with gpt
     */
    public static void main(String[] args) {

        // ---- 1. Create a fake DAO with some demo tasks ----
        use_cases.TaskListDataAccessInterface dao = new data_access.InMemoryTaskListDataAccessObject();

        entity.Task t1 = entity.Task.TaskFactory.createTask("Test Task A");
        entity.Task t2 = entity.Task.TaskFactory.createTask("Test Task B");
        dao.saveAllTasks(java.util.Arrays.asList(t1, t2));

        // ---- 2. Build view model + presenter ----
        interface_adapter.TaskListViewModel viewModel = new interface_adapter.TaskListViewModel();
        use_cases.TaskListOutputBoundary presenter =
                new interface_adapter.TaskListPresenter(viewModel);

        // ---- 3. Build interactor + controller ----
        use_cases.TaskListInputBoundary interactor =
                new use_cases.TaskListInteractor(dao, presenter);

        interface_adapter.TaskListController taskController =
                new interface_adapter.TaskListController(interactor);

        // ---- 4. Dummy SetTimerController (so timer button won't crash) ----
        interface_adapter.SetTimerController dummyTimerController =
                new interface_adapter.SetTimerController((taskName) -> {
                    System.out.println("Timer set for " + taskName +
                            " for minutes.");
                });

        // ---- 5. Build actual UI panel ----
        TaskListView panel = new TaskListView(taskController, viewModel, dummyTimerController);

        // ---- 6. Put into a JFrame ----
        javax.swing.JFrame frame = new javax.swing.JFrame("TaskListPanel Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }

}
