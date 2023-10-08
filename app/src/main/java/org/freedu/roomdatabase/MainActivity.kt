package org.freedu.roomdatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.freedu.roomdatabase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var studentDatabase: StudentDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        studentDatabase = StudentDatabase.getDatabase(this)
        binding.saveBtn.setOnClickListener {
            saveData()
        }
        binding.searchBtn.setOnClickListener {
            searchData()
        }
        binding.deleteallBtn.setOnClickListener {
            GlobalScope.launch {
                studentDatabase.studentDao().deleteAll()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun searchData() {
        val rollNo = binding.rollNoSrEtx.text.toString()
        if(rollNo.isNotEmpty()){
            lateinit var student: Student
            GlobalScope.launch{
                student = studentDatabase.studentDao().findByRoll(rollNo.toInt())
                if(studentDatabase.studentDao().isEmpty())
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@MainActivity,"Database have no data", Toast.LENGTH_SHORT).show()
                    }

                else{
                    displayData(student)
                }
            }
        }
    }

    private suspend fun displayData(student: Student) {
        withContext(Dispatchers.Main){
            binding.firstNameEtx.setText(student.firstName.toString())
            binding.lastNameEtx.setText(student.lastName.toString())
            binding.rollNoEtx.setText(student.rollNo.toString())
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveData() {
        val firstName = binding.firstNameEtx.text.toString()
        val lastName = binding.lastNameEtx.text.toString()
        val rollNo = binding.rollNoEtx.text.toString()

        if(firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){
            val student = Student(null, firstName, lastName, rollNo.toInt())
            GlobalScope.launch (Dispatchers.IO ){
                studentDatabase.studentDao().insert(student)
            }
            binding.firstNameEtx.text.clear()
            binding.lastNameEtx.text.clear()
            binding.rollNoEtx.text.clear()

            Toast.makeText(this@MainActivity,"Data saved", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@MainActivity,"Please enter all the data", Toast.LENGTH_SHORT).show()
        }

    }
}