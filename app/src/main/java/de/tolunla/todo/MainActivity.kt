package de.tolunla.todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.tolunla.todo.databinding.ActivityMainBinding
import de.tolunla.todo.databinding.DialogTaskInputBinding
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var taskDialogBinding: DialogTaskInputBinding
    private lateinit var materialDialogBuilder: MaterialAlertDialogBuilder

    private val taskList = mutableListOf<String>()
    private val taskListAdapter = TaskListAdapter(taskList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        materialDialogBuilder = MaterialAlertDialogBuilder(this)

        binding.fabAdd.setOnClickListener {
            taskDialogBinding = DialogTaskInputBinding.inflate(LayoutInflater.from(this))
            materialDialogBuilder.setView(taskDialogBinding.root)
                .setTitle(R.string.new_task)
                .setPositiveButton(R.string.add) { dialog, _ ->
                    taskList.add(taskDialogBinding.taskName.text.toString())
                    taskListAdapter.notifyItemInserted(taskList.size - 1)
                    saveData(this, taskList)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }

        binding.taskList.adapter = taskListAdapter
        binding.taskList.setHasFixedSize(true)

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val file = getDataFile(this)

        if (file.exists()) {
            try {
                taskList.addAll(Json.decodeFromStream<List<String>>(file.inputStream()))
                taskListAdapter.notifyItemRangeInserted(0, taskList.size)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: SerializationException) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        fun showSoftKeyboard(view: View) {
            if (view.requestFocus()) {
                val imm: InputMethodManager =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        private fun getDataFile(context: Context): File {
            val file = File(context.filesDir.absolutePath, "tasks.json")

            if (!file.exists()) file.createNewFile()

            return file
        }

        fun saveData(context: Context, taskList: List<String>) {
            val file = getDataFile(context)

            try {
                Json.encodeToStream(taskList, file.outputStream())
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: SerializationException) {
                e.printStackTrace()
            }

        }
    }
}
