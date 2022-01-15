package de.tolunla.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.tolunla.todo.MainActivity.Companion.showSoftKeyboard
import de.tolunla.todo.databinding.DialogTaskInputBinding

class TaskListAdapter(private val taskList: MutableList<String>) :
    RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    lateinit var taskDialogBinding: DialogTaskInputBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.title.text = taskList[position]

        holder.itemView.setOnLongClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("${context.getString(R.string.removing)} ${taskList[position]}")
                .setPositiveButton(R.string.remove) { dialog, _ ->
                    taskList.removeAt(position)
                    notifyItemRemoved(position)
                    MainActivity.saveData(context, taskList)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .show()

            true
        }

        holder.itemView.setOnClickListener {
            taskDialogBinding = DialogTaskInputBinding.inflate(LayoutInflater.from(context))
            taskDialogBinding.taskName.setText(taskList[position])

            MaterialAlertDialogBuilder(context).setView(taskDialogBinding.root)
                .setTitle(R.string.rename_task)
                .setPositiveButton(R.string.rename) { dialog, _ ->
                    taskList[position] = taskDialogBinding.taskName.text.toString()
                    notifyItemChanged(position)
                    MainActivity.saveData(context, taskList)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .show()

            showSoftKeyboard(taskDialogBinding.taskName)
        }
    }

    override fun getItemCount() = taskList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(android.R.id.text1)
    }
}