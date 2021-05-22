package com.example.myapplicationloader1
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private val CONTACTS_SUMMARY_PROJECTION: Array<String> = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.CONTACT_STATUS,
            ContactsContract.Contacts.CONTACT_PRESENCE,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.Contacts.LOOKUP_KEY
    )
    private lateinit var mAdapter: SimpleCursorAdapter
    private var curFilter: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(0,null,this)
        setContentView(R.layout.activity_main)
        val lv: ListView = findViewById(R.id.list)
        mAdapter = SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                null,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.CONTACT_STATUS),
                intArrayOf(android.R.id.text1, android.R.id.text2),
                0
        )
        lv.adapter = mAdapter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Search")?.apply {
            setIcon(android.R.drawable.ic_menu_search)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = SearchView(this@MainActivity).apply {
                setOnQueryTextListener(this@MainActivity)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }
    fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        // Insert desired behavior here.
        Log.i("ActivityComplexList", "Item clicked: $id")
    }
    override fun onQueryTextSubmit(query: String?): Boolean {
     return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        curFilter = if (newText?.isNotEmpty() == true) newText else null
        LoaderManager.getInstance(this).restartLoader(0,null,this)
        return true
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val baseUri: Uri = if (curFilter != null) {
            Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, Uri.encode(curFilter))
        } else {
            ContactsContract.Contacts.CONTENT_URI
        }
        val select: String = "((${ContactsContract.Contacts.DISPLAY_NAME} NOTNULL) AND (" +
                "${ContactsContract.Contacts.HAS_PHONE_NUMBER}=1) AND (" +
                "${ContactsContract.Contacts.DISPLAY_NAME} != ''))"
        return (this as? Context)?.let { context ->
            CursorLoader(
                    context,
                    baseUri,
                    CONTACTS_SUMMARY_PROJECTION,
                    select,
                    null,
                    "${ContactsContract.Contacts.DISPLAY_NAME} COLLATE LOCALIZED ASC"
            )
        } ?: throw Exception("Activity cannot be null")

    }
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mAdapter.swapCursor(data)
    }
    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.swapCursor(null)
    }
}
