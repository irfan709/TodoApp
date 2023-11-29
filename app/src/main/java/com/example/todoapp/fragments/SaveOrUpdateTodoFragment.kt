package com.example.todoapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.todoapp.data.TodoModel
import com.example.todoapp.databinding.FragmentSaveOrUpdateTodoBinding
import com.example.todoapp.enums.Priority
import com.example.todoapp.viewmodel.TodoActivityViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SaveOrUpdateTodoFragment : Fragment() {

    private lateinit var binding: FragmentSaveOrUpdateTodoBinding
    private lateinit var navController: NavController
    private val todoActivityViewModel: TodoActivityViewModel by activityViewModels()
    private val args: SaveOrUpdateTodoFragmentArgs by navArgs()
    private lateinit var result: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveOrUpdateTodoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.addtobtn.setOnClickListener {
            addTodo()
        }

        binding.backarrow.setOnClickListener {
            navController.popBackStack()
        }

        binding.duedatelayout.setOnClickListener {
            setDueDate()
        }

        displayTodo()
    }

    private fun setDueDate() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }

                if (selectedDate.timeInMillis >= System.currentTimeMillis()) {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.duedatetv.text = formattedDate
                } else {
                    showToast("Task should be in a future date")
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun displayTodo() {
        val todo = args.todo

        if (todo != null) {
            binding.tasknameet.setText(todo.name)
            binding.taskdescet.setText(todo.description)
            binding.duedatetv.text = todo.date
        }
    }

    private fun addTodo() {
        if (binding.tasknameet.text?.isEmpty() == true) {
            binding.taskname.error = "Title is required"
            showToast("Title is required")
            return
        } else if (binding.duedatetv.text?.toString()
                ?.isEmpty() == true || !isValidDate(binding.duedatetv.text.toString())
        ) {
            showToast("Select a valid due date")
            return
        } else if (binding.taskdescet.text?.isEmpty() == true) {
            val selectedDate = binding.duedatetv.text.toString()
            val todo = args.todo

            when (todo) {
                null -> {
                    todoActivityViewModel.insertTodo(
                        TodoModel(
                            0,
                            binding.tasknameet.text.toString(),
                            "",
                            selectedDate,
                            Priority.LOW,
                            false
                        )
                    )
                    result = "Todo Saved"
                    showToast("Todo Saved Successfully")
                }

                else -> {
                    updateTodo()
                    showToast("Todo Updated Successfully")
                }
            }

            navController.navigate(SaveOrUpdateTodoFragmentDirections.actionSaveOrUpdateTodoFragmentToTodoFragment())
        } else {
            val selectedDate = binding.duedatetv.text.toString()
            val todo = args.todo

            when (todo) {
                null -> {
                    todoActivityViewModel.insertTodo(
                        TodoModel(
                            0,
                            binding.tasknameet.text.toString(),
                            binding.taskdescet.text.toString(),
                            selectedDate,
                            Priority.LOW,
                            false
                        )
                    )
                    result = "Todo Saved"
                    showToast("Todo Saved Successfully")
                }

                else -> {
                    updateTodo()
                    showToast("Todo Updated Successfully")
                }
            }

            navController.navigate(SaveOrUpdateTodoFragmentDirections.actionSaveOrUpdateTodoFragmentToTodoFragment())
        }
    }

    private fun updateTodo() {
        val todo = args.todo
        val selectedDate = binding.duedatetv.text.toString()

        if (todo != null) {
            todoActivityViewModel.updateTodo(
                TodoModel(
                    todo.id,
                    binding.tasknameet.text.toString(),
                    binding.taskdescet.text.toString(),
                    selectedDate,
                    todo.priority,
                    todo.status
                )
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidDate(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        try {
            dateFormat.parse(dateString)
            return true
        } catch (e: ParseException) {
            return false
        }
    }
}