package com.coderschool.vinh.todoapp.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.coderschool.vinh.todoapp.R;
import com.coderschool.vinh.todoapp.adapter.TaskAdapter;
import com.coderschool.vinh.todoapp.fragments.TaskDialog;
import com.coderschool.vinh.todoapp.models.DialogResponse;
import com.coderschool.vinh.todoapp.models.Task;
import com.coderschool.vinh.todoapp.repositories.LocalDBHandler;
import com.coderschool.vinh.todoapp.repositories.TaskPreferences;

import java.util.ArrayList;

import static com.coderschool.vinh.todoapp.fragments.TaskDialog.FRAGMENT_EDIT_NAME;

public class MainActivity extends AppCompatActivity
        implements TaskDialog.TaskDialogOnFinishedListener,
        AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener {
    private FloatingActionButton fab;
    private ListView lvTasks;

    private TaskAdapter adapter;
    private ArrayList<Task> tasks;

    private LocalDBHandler dbTasks;
    private TaskPreferences taskPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        dbTasks = new LocalDBHandler(this);
        taskPreferences = new TaskPreferences(MainActivity.this);

        lvTasks = (ListView) findViewById(R.id.list_task_item);
        lvTasks.setOnItemLongClickListener(this);
        lvTasks.setOnItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.FloatingActionButton);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tasks = dbTasks.getAllTasks();
        adapter = new TaskAdapter(this, tasks);
        lvTasks.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        if (dbTasks != null) {
            dbTasks.refreshAllTasks(tasks);
        }
        super.onPause();
    }

    private void showTaskDialog(Task task) {
        TaskDialog editNameDialogFragment
                = task != null
                ? TaskDialog.newInstance(task)
                : TaskDialog.newInstance();
        editNameDialogFragment.show(getSupportFragmentManager(), FRAGMENT_EDIT_NAME);
    }

    @Override
    public void onTaskDialogFinished(DialogResponse response) {
        // response.getIsChangeable() == true means add one task in todoList.
        // Otherwise, a task in (int) position will be modified in new window.
        if (!response.getIsChangeable()) {
            adapter.addTask(0, response.getTask());
        } else {
            int position = taskPreferences.getCurrentPosition();
            adapter.modifyTask(position, response.getTask());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.removeTask(position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        taskPreferences.setCurrentPosition(position);
        showTaskDialog(tasks.get(position));
    }

    @Override
    public void onClick(View v) {
        showTaskDialog(null);
    }
}