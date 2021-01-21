package me.mateus.firebasestoragetest

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog

class MainActivity : AppCompatActivity() {

    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference
    private val IMAGE_LOAD_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        config()

        findViewById<Button>(R.id.btnEnviar).setOnClickListener { openFileChooser() }
    }

    fun config() {
        alertDialog = SpotsDialog.Builder().setContext(this).build()
        storageReference = FirebaseStorage.getInstance().getReference("imagem_firebase")
    }

    fun openFileChooser() {
        Intent().also { intent ->
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Carregar Imagem"), IMAGE_LOAD_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                IMAGE_LOAD_CODE -> {
                    data?.data?.also { uri ->
                        alertDialog.show()
                        storageReference.putFile(uri).also { uploadTask ->
                            uploadTask.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let { throw it }
                                }
                                storageReference.downloadUrl
                            }.addOnCompleteListener { task ->
                                alertDialog.dismiss()
                                if (task.isSuccessful) {
                                    Picasso.get().load(task.result).into(findViewById<ImageView>(R.id.imgImagem))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}