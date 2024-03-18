package com.example.masjidfirebaseapp.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.masjidfirebaseapp.R
import com.example.masjidfirebaseapp.databinding.ActivityMainBinding
import com.example.masjidfirebaseapp.model.Jamaah
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : AppCompatActivity(), JamaahAdapter.FireBaseDataListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: JamaahAdapter
    private lateinit var db: FirebaseDatabase
    private lateinit var jamaahRef: DatabaseReference
    private lateinit var listJamaah: ArrayList<Jamaah>
    private lateinit var mToolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mToolbar = binding.toolbar
        setSupportActionBar(mToolbar)

        //setup recyclerview
        binding.rvJamaah.layoutManager = LinearLayoutManager(this)
        binding.rvJamaah.setHasFixedSize(true)

        //setup firebase
        db = Firebase.database
        jamaahRef = db.reference.child(JAMAAH_CHILD)
        jamaahRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listJamaah = ArrayList()
                Log.d("Snapshot", "$snapshot")
                for (jamaahSnap in snapshot.children) {
                    jamaahSnap.getValue(Jamaah::class.java)?.let { jamaah ->
                        jamaah.id = jamaahSnap.key.toString()
                        listJamaah.add(jamaah)
                    }
                }
                Log.d("List Jamaah", "$listJamaah")

                adapter = JamaahAdapter(this@MainActivity, listJamaah)
                binding.rvJamaah.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("${error.details} ${error.message}")
            }
        })

        binding.fabAdd.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val bundle = Bundle()
            bundle.putBoolean(AddUpdateFragment.IS_UPDATE, false)
            val addDialogFragment = AddUpdateFragment()
            addDialogFragment.arguments = bundle
            addDialogFragment.show(fragmentManager, AddUpdateFragment::class.java.simpleName)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)

        mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    val menuItemSearch = menu?.findItem(R.id.search)
                    val searchView: SearchView = menuItemSearch?.actionView as SearchView
                    searchView.queryHint = "Cari Jamaah"
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            searchJamaah(newText)
                            return true
                        }

                    })
                    true
                }

                else -> false
            }
        }
        return true
    }

    private fun searchJamaah(keyword: String?) {
        if (keyword != null) {
            val searchedJamaah = listJamaah.filter {
                it.nama?.contains(keyword, true) == true
                        || it.alamat?.contains(keyword, true) == true
            }
            if (searchedJamaah.isEmpty()) {
                showToast(getString(R.string.no_jamaah))
            } else {
                adapter.setSearchedList(searchedJamaah as ArrayList<Jamaah>)
            }
        }
    }

    override fun onDataClick(jamaah: Jamaah?) {
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilder
            .setTitle(getString(R.string.pilih_aksi))
            .setPositiveButton(getString(R.string.update)) { _, _ ->
                val bundle = Bundle()
                bundle.putBoolean(AddUpdateFragment.IS_UPDATE, true)
                bundle.putParcelable(AddUpdateFragment.EXTRA_JAMAAH, jamaah)
                val updateDialog = AddUpdateFragment()
                updateDialog.arguments = bundle
                updateDialog.show(supportFragmentManager, AddUpdateFragment::class.java.simpleName)
            }
            .setNegativeButton(getString(R.string.delete)) { _, _ -> hapusJamaah(jamaah) }
            .setNeutralButton(getString(R.string.batal)) { dialog, _ -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun hapusJamaah(jamaah: Jamaah?) {
        jamaah?.id?.let {
            jamaahRef.child(it).removeValue().addOnSuccessListener {
                showToast(getString(R.string.deleted))
            }
        }
    }

    internal var finishDialogListener = object : AddUpdateFragment.OnFinishDialogListener {
        override fun onFinishDialog(text: String?) {
            showToast(text)
        }
    }

    private fun showToast(text: String?) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val JAMAAH_CHILD = "jamaah"
    }
}