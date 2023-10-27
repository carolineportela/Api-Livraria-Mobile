package br.senai.sp.jandira.api_livraria

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CadastroLivroImagem : AppCompatActivity() {

    //ATRIBUTOS PARA MANIPULACAO (OBJETOS DE URI)
    private var imageUriGRD: Uri? = null
    private var imageUriPEQ: Uri? = null

    //ATRIBUTO PARA ACESSO E MANIPULACAO DO STORAGE
    private lateinit var storageRef: StorageReference

    //ATRIBUTO PARA ACESSO E MANIPULACAO DO FURESRORE DATABASE
    private lateinit var firebaseFireStore: FirebaseFirestore

    // ATRIBUTOS DE IMAGEVIEW
    private var btnImgGRD: ImageView? = null
    private var btnImgPEQ: ImageView? = null

    //atributos de button
    private var btnUpload : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastro_livro_imagem)

        //INICIALIZA AS VARIAVEIS DO FIRESTORE
        initVars()

        //TESTE DE RECEBEBIMENTO DE CORPO DE DADOS JSON
        val body = intent.getStringExtra("bodyJSON")
        Log.e("TESTE-JSON",body.toString())

        //RECUPERA OS OBJETOS DE VIEW DAS IMAGENS
        btnImgGRD = findViewById(R.id.imgGRD)
        btnImgPEQ = findViewById(R.id.imgPEQ)

        //RECUPERA O OBJETO DE BUTTON PARA REALIZAR UPLOUD
        btnUpload = findViewById(R.id.btnCadastrarLivro)

        //RECUPERA A IMAGEM GRANDE DA GALERIA
        btnImgGRD?.setOnClickListener {
            resultLauncherGRD.launch("image/*")
        }


        //RECUPERA A IMAGEM PEQUENADA GALERIA
        btnImgPEQ?.setOnClickListener {
            resultLauncherPQ.launch("image/*")
        }

        btnUpload?.setOnClickListener {
            upload()
        }
    }

     //INICIALIZA OS ATRIBUTOS REFERENTES AO FIREBASE
     private fun initVars(){
         //INICIALIZA O STORAGE COM A PASTA DE IMAGENS
         storageRef = FirebaseStorage.getInstance().reference.child("images")
         firebaseFireStore = FirebaseFirestore.getInstance()
     }

    //LANCADOR PARA PEGAR AS IAGENS GRANDES DA GALERIA
    private val resultLauncherGRD = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUriGRD = it
        btnImgGRD?.setImageURI(it)
        Log.e("IMG-GRANDE",it.toString())
    }


    //LANCADOR PARA PEGAR AS IAGENS GRANDES DA GALERIA
    private val resultLauncherPQ = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUriPEQ = it
        btnImgPEQ?.setImageURI(it)
        Log.e("IMG-PEQUENA",it.toString())
    }

    //UPLOAD DAS IMAGENS
    private fun upload(){
        imageUriGRD?.let {
            val riversRef = storageRef.child("${it.lastPathSegment}-${System.currentTimeMillis()}.jpg")
            val uploadTask = riversRef.putFile(it)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    riversRef.downloadUrl.addOnSuccessListener { uri ->
                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()
                        firebaseFireStore.collection("images").add(map).addOnCompleteListener { firestoreTask ->
                            if (firestoreTask.isSuccessful){
                                Toast.makeText(this, "UPLOAD IMAGEM GRANDE OK!", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this, firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                            btnImgGRD?.setImageResource(R.drawable.upload)
                        }
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    btnImgGRD?.setImageResource(R.drawable.upload)
                }
            }
        }
    }


}