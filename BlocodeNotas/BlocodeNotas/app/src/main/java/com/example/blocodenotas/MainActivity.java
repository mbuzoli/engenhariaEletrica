package com.example.blocodenotas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper dh;
    EditText etNome, etEmp, etEnd;
    Button btInserir,btListar;

    EditText ettext;
    Button btcriar;
    ListView listView;

    private ArrayList<String> notas = new ArrayList<String>();
    ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.dh = new DBHelper(this);
        etNome = (EditText) findViewById(R.id.etNome);
        etEnd = (EditText) findViewById(R.id.etEndereco);
        etEmp = (EditText) findViewById(R.id.etEmpresa);
        btInserir = (Button) findViewById(R.id.btInserir);
        btListar = (Button) findViewById(R.id.btListar);
    btInserir.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(etNome.getText().length()>0 && etEnd.getText().length()>0 && etEmp.getText().length()>0){

                dh.insert(etNome.getText().toString(),etEnd.getText().toString(),etEmp.getText().toString());
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Sucesso");
                adb.setMessage("Cadastro realizado");
                adb.show();

                //limpar campos
                etNome.setText("");
                etEnd.setText("");
                etEmp.setText("");
            }else{
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Erro");
                adb.setMessage("Todos os campos precisam ser preenchidos");
                adb.show();
            }

            }

    });
    btListar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<Contato> contatos = dh.queryGetAll();
            if  (contatos == null){
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Mensagem");
                adb.setMessage("Náo há registros cadastrados!");
                adb.show();
                return;
            }
            for (int i=0; i<contatos.size();i++){
                Contato contato = (Contato) contatos.get(i);
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Registro"+(i+1));
                adb.setMessage("Nome: "+ contato.getNome()
                + "\nEndereço: "+ contato.getEndereco()
                + "\nEmpresa: "+ contato.getEmpresa());
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.show();
            }
    }});
}}