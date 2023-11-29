package com.example.todoapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.TodoModel
import com.example.todoapp.databinding.ItemTodoBinding
import com.example.todoapp.enums.Priority
import com.example.todoapp.fragments.TodoFragmentDirections
import com.example.todoapp.viewmodel.TodoActivityViewModel

class TodoAdapter(private val viewModel: TodoActivityViewModel) :
    ListAdapter<TodoModel, TodoAdapter.TodoViewHolder>(DiffUtilCallBack()) {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTodoBinding.bind(itemView)
        val title: TextView = binding.tasktv
        val completedImg: ImageView = binding.completedimg
        val date: TextView = binding.taskdate
        val dateCard: CardView = binding.datecard
        val mainCard: CardView = binding.maincard
        val priorityImg: View = binding.taskpriorityimg
        val checkBox: CheckBox = binding.taskstatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        getItem(position).let { todo ->
            holder.apply {
                mainCard.transitionName = "recyclerView_${todo.id}"
                title.text = todo.name
                date.text = todo.date

                holder.checkBox.setOnCheckedChangeListener(null)
                holder.checkBox.isChecked = todo.status

                completedImg.visibility = if (checkBox.isChecked) View.VISIBLE else View.GONE

                holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    getItem(position).let { todo ->
                        todo.status = isChecked
                        completedImg.visibility = if (isChecked) View.VISIBLE else View.GONE
                        viewModel.updateCompletionStatus(todo.id, isChecked)
                    }
                }

                holder.itemView.setOnLongClickListener { view ->
                    val popupMenu = PopupMenu(view.context, view)
                    popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.lowmenu -> {
                                viewModel.setTodoPriority(todoId = todo.id, Priority.LOW)
                                true
                            }
                            R.id.mediummenu -> {
                                viewModel.setTodoPriority(todoId = todo.id, Priority.MEDIUM)
                                true
                            }
                            R.id.highmenu -> {
                                viewModel.setTodoPriority(todoId = todo.id, Priority.HIGH)
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.show()

                    true
                }

                when (todo.priority) {
                    Priority.LOW -> {
                        priorityImg.setBackgroundResource(R.drawable.green_dot)
                    }

                    Priority.MEDIUM -> {
                        priorityImg.setBackgroundResource(R.drawable.yellow_dot)
                    }

                    Priority.HIGH -> {
                        priorityImg.setBackgroundResource(R.drawable.red_dot)
                    }

                    else -> {
                        dateCard.setCardBackgroundColor(Color.MAGENTA)
                        mainCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.black))
                    }
                }

                itemView.setOnClickListener { view ->
                    val action =
                        TodoFragmentDirections.actionTodoFragmentToSaveOrUpdateTodoFragment()
                            .setTodo(todo)
                    val extras = FragmentNavigatorExtras(mainCard to "recyclerView_${todo.id}")
                    Navigation.findNavController(view).navigate(action, extras)
                }

                title.setOnClickListener { view ->
                    val action =
                        TodoFragmentDirections.actionTodoFragmentToSaveOrUpdateTodoFragment()
                            .setTodo(todo)
                    val extras = FragmentNavigatorExtras(mainCard to "recyclerView_${todo.id}")
                    Navigation.findNavController(view).navigate(action, extras)
                }
            }
        }
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<TodoModel>() {
        override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
            return oldItem == newItem
        }
    }
}