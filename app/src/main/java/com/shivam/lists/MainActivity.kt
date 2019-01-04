package com.shivam.lists

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.CardView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity(){
    private var notesArray : ArrayList<String> = ArrayList()
    private lateinit var arrayAdapter : RecyclerView.Adapter<*>
    private lateinit var mLayoutManager : RecyclerView.LayoutManager
    private var arrayPos : Int? = null
    private lateinit var sp : SharedPreferences
    var deletedItemIndex : Int? = null // For undo action of the Snackbar when a note is deleted
    var deletedItemString : String? = null   // For undo action of the Snackbar when a note is deleted

    private fun <T> toMutableSet(arrayList: ArrayList<T>): MutableSet<T>{
        val set : MutableSet<T> = mutableSetOf()
        set.addAll(arrayList)
        return set
    }
    private fun <T> toArrayList(mutableSet: MutableSet<T>): ArrayList<T>{
        val arrayList = arrayListOf<T>()
        arrayList.addAll(mutableSet)
        return arrayList
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp = getSharedPreferences("notesPref", Context.MODE_PRIVATE)
        notesArray = toArrayList(sp.getStringSet("notesArray", mutableSetOf<String>()))
        // Setting up the RecyclerView
        arrayAdapter = myViewAdapter(notesArray)
        mLayoutManager = LinearLayoutManager(this)
        val mRecyclerView : RecyclerView = findViewById<RecyclerView>(R.id.notesListView).apply {
            layoutManager = mLayoutManager
            adapter = arrayAdapter
            addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        }
        class undoListener : View.OnClickListener{
            override fun onClick(v: View?) {
                notesArray.add(deletedItemIndex!!, deletedItemString!!)
                arrayAdapter.notifyDataSetChanged()

            }

        }
        val simpleItemTouchCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                val index = p0.adapterPosition
                deletedItemIndex = p0.adapterPosition
                deletedItemString = notesArray[index]
                notesArray.removeAt(index)
                arrayAdapter.notifyDataSetChanged()
                Snackbar.make(findViewById(R.id.rootMain), "1 Note deleted. Undo", Snackbar.LENGTH_LONG).setAction("Undo", undoListener()).show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
        val context = this
        // Code for Detecting Long press on ListView Items
        mRecyclerView.addOnItemTouchListener(RecyclerTouchListener(this, mRecyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onLongClick(view: View?, position: Int) {
                AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes") { _, _ ->
                            notesArray.removeAt(position)
                            arrayAdapter.notifyDataSetChanged()
                        }
                        .setNegativeButton("No", null)
                        .show()            }

            override fun onClick(view: View, position: Int) {
                val intent = Intent(applicationContext, notesActivity::class.java)
                arrayPos = position
                intent.putExtra("note", ((view as CardView).getChildAt(0) as TextView).text.toString())
                startActivityForResult(intent, 1)

            }
        }))

        // Code for detect that Button has been clicked and launch notesActivity
        val floatingButton : FloatingActionButton = findViewById(R.id.floatingButton)
        floatingButton.setOnClickListener {
            arrayPos = null
            val intent = Intent(applicationContext, notesActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }
    // Code for getting back data from notesActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                data!!
                if (arrayPos == null)
                {
                    notesArray.add(data.getStringExtra("note"))
                }
                else
                {
                    notesArray[arrayPos!!] = data.getStringExtra("note")
                }
                arrayAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sp.edit().putStringSet("notesArray", toMutableSet(notesArray)).apply()
    }
}

