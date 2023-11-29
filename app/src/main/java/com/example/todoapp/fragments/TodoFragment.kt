package com.example.todoapp.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.adapter.TodoAdapter
import com.example.todoapp.database.TodoDatabase
import com.example.todoapp.databinding.FragmentTodoBinding
import com.example.todoapp.factory.TodoActivityViewModelFactory
import com.example.todoapp.repository.TodoRepository
import com.example.todoapp.utils.SwipeToDelete
import com.example.todoapp.viewmodel.TodoActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TodoFragment : Fragment() {

    private lateinit var todobinding: FragmentTodoBinding
    private lateinit var todoAdapter: TodoAdapter

    private fun getTodoActivityViewModel(): TodoActivityViewModel {
        val activity = requireActivity() as MainActivity
        val todoRepository = TodoRepository(TodoDatabase.createDatabase(activity))
        val todoActivityViewModelFactory = TodoActivityViewModelFactory(todoRepository)
        return ViewModelProvider(
            activity,
            todoActivityViewModelFactory
        )[TodoActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        todobinding = FragmentTodoBinding.inflate(inflater, container, false)
        return todobinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialElevationScale(true).apply {
            duration = 300
        }
        exitTransition = MaterialElevationScale(false).apply {
            duration = 300
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        val navController = Navigation.findNavController(view)

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            activity.window.statusBarColor = Color.parseColor("#9E9D9D")
        }

        todobinding.addtodofab.setOnClickListener {
            todobinding.addtodofab.visibility = View.INVISIBLE
            navController.navigate(TodoFragmentDirections.actionTodoFragmentToSaveOrUpdateTodoFragment())
        }

        todoAdapter = TodoAdapter(getTodoActivityViewModel())
        todobinding.todorv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        todobinding.todorv.setHasFixedSize(true)
        todoAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        todobinding.todorv.adapter = todoAdapter

        getTodoActivityViewModel().getAllTodo().observe(viewLifecycleOwner) { todo ->
            todobinding.notasklayout.isVisible = todo.isEmpty()
            todoAdapter.submitList(todo)
        }

        swipeToDelete(todobinding.todorv)
    }

    private fun swipeToDelete(rvnote: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val todo = todoAdapter.currentList[position]

                val snackBar = Snackbar.make(
                    requireView(),
                    "Task Deleted...", Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("Undo") {

                            getTodoActivityViewModel().insertTodo(todo)
                            observerDataChanges()
                        }
                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.addtodofab)
                }

                snackBar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                snackBar.show()

                getTodoActivityViewModel().deleteTodo(todo)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvnote)
    }

    private fun observerDataChanges() {
        getTodoActivityViewModel().getAllTodo().observe(viewLifecycleOwner) { todoList ->
            todobinding.notasklayout.isVisible = todoList.isEmpty()

            if (todoList.size > todoAdapter.currentList.size) {
                val restoredTodo = todoList.find { it !in todoAdapter.currentList }
                Toast.makeText(
                    requireContext(),
                    "Task Restored: ${restoredTodo?.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            todoAdapter.submitList(todoList)
        }
    }
}