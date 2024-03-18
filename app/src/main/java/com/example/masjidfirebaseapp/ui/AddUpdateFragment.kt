package com.example.masjidfirebaseapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.masjidfirebaseapp.R
import com.example.masjidfirebaseapp.databinding.FragmentAddUpdateBinding
import com.example.masjidfirebaseapp.helper.Utils.getCurrentDate
import com.example.masjidfirebaseapp.model.Jamaah
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddUpdateFragment : DialogFragment() {

    private var _binding: FragmentAddUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var jamaahRef: DatabaseReference
    private var listener: OnFinishDialogListener? = null

    private var isUpdate: Boolean? = false
    private var jamaah: Jamaah? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUpdateBinding.inflate(inflater, container, false)
        jamaahRef = FirebaseDatabase.getInstance().getReference(MainActivity.JAMAAH_CHILD)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        jamaah = bundle?.getParcelable<Jamaah>(EXTRA_JAMAAH) ?: Jamaah()
        isUpdate = bundle?.getBoolean(IS_UPDATE)

        val actionBarTitle: String

        if (isUpdate!!) {
            actionBarTitle = getString(R.string.update)
            if (jamaah != null) {
                jamaah?.let { jamaah ->
                    binding.apply {
                        edNamaJamaah.setText(jamaah.nama)
                        edAlamatJamaah.setText(jamaah.alamat)
                    }
                }
            }
        } else {
            actionBarTitle = getString(R.string.tambah)
        }

        binding.tvTitle.text = actionBarTitle

        binding.btnChoose.setOnClickListener {
            val nama = binding.edNamaJamaah.text.toString()
            val alamat = binding.edAlamatJamaah.text.toString()
            val tglInput = if (isUpdate!!) jamaah?.tglInput else getCurrentDate()

            if (nama.isNotEmpty() && alamat.isNotEmpty()) {
                jamaah?.let { jamaah ->
                    jamaah.nama = nama
                    jamaah.alamat = alamat
                    jamaah.tglInput = tglInput
                }

                if (isUpdate!!) {
                    Log.d(getString(R.string.update), "$jamaah")
                    updateDataJamaah(jamaah)
                } else {
                    Log.d(getString(R.string.tambah), "$jamaah")
                    submitDataJamaah(jamaah)
                }

            } else {
                if (nama.isEmpty()) {
                    binding.edNamaJamaah.error = getString(R.string.empty_field)
                }
                if (alamat.isEmpty()) {
                    binding.edAlamatJamaah.error = getString(R.string.empty_field)
                }
            }
        }

        binding.btnClose.setOnClickListener {
            dialog?.cancel()
        }

    }

    override fun onResume() {
        super.onResume()
        val params = dialog!!.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params
    }

    private fun submitDataJamaah(jamaah: Jamaah?) {
        jamaahRef.push().setValue(jamaah).addOnSuccessListener {
            listener?.onFinishDialog(getString(R.string.added))
            dialog?.dismiss()
        }
    }

    private fun updateDataJamaah(jamaah: Jamaah?) {
        jamaah?.id?.let {
            jamaahRef.child(it).setValue(jamaah).addOnSuccessListener {
                listener?.onFinishDialog(getString(R.string.updated))
                dialog?.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = requireActivity()
        if (activity is MainActivity) {
            this.listener = activity.finishDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.listener = null
    }

    interface OnFinishDialogListener {
        fun onFinishDialog(text: String?)
    }

    companion object {
        const val EXTRA_JAMAAH = "extra_jamaah"
        const val IS_UPDATE = "is_update"
    }
}