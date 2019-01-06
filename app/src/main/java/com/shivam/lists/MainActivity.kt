package com.shivam.lists

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var listsArray = arrayListOf<String>()
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<*>
    private lateinit var sp : SharedPreferences
    fun onClickAddButton(){
        val input = EditText(this)
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setView(input)
            setPositiveButton("OK"){ dialog: DialogInterface?, which: Int ->
                if (input.text.isEmpty()){
                    Toast.makeText(context, "List name cannot be empty", Toast.LENGTH_LONG).show()
                }
                else
                {
                    val mIntent = Intent(applicationContext, ListActivity::class.java)
                    listsArray.add(input.text.toString())
                    recyclerViewAdapter.notifyDataSetChanged()
                    val str = input.text.toString().replace(' ', '_')
                    mIntent.putExtra("listName", str)
                    startActivity(mIntent)
                }
            }
            setNegativeButton("Cancel", null)
            setMessage("Enter name of the list")
            setTitle("Name")
            setIcon(android.R.drawable.ic_dialog_info)
        }
        builder.show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Lists"
        sp = getSharedPreferences("listsArray", Activity.MODE_PRIVATE)
        listsArray = Gson().fromJson(sp.getString("array", "[]"), listsArray.javaClass)
        Log.i("array", listsArray.toString())
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMain)
        recyclerViewAdapter = myViewAdapter(listsArray)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.apply {
            adapter = recyclerViewAdapter
            setLayoutManager(layoutManager)
            addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
            addOnItemTouchListener(RecyclerTouchListener(applicationContext, this, object : RecyclerTouchListener.ClickListener{
                override fun onClick(view: View, position: Int) {
                    val mIntent = Intent(applicationContext, ListActivity::class.java)
                    mIntent.putExtra("listName", ((view as CardView).getChildAt(0) as TextView).text.toString())
                    startActivity(mIntent)
                }

                override fun onLongClick(view: View?, position: Int) {}
            }))
        }

        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            onClickAddButton()
        }


    }

    override fun onPause() {
        super.onPause()
        val json = Gson()
        sp.edit().putString("array", json.toJson(listsArray)).apply()
    }
}
