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

    private Button btnCriarNota;
    private ListView listViewNotas;
    private EditText etNota;
    private ArrayList<String> notas = new ArrayList<String>();
    private ArrayAdapter<String> adapter = null;
    private DBHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCriarNota = (Button) findViewById(R.id.btnCriarNota);
        listViewNotas = (ListView) findViewById(R.id.listViewNotas);
        etNota = (EditText) findViewById(R.id.etNota);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,notas);
        listViewNotas.setAdapter(adapter);
        dbHelper = new DBHelper(this);
        populateListViewFromDB();



    btnCriarNota.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String textoNota = etNota.getText().toString();
            if (ExistNoteDB(textoNota)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Erro");
                alert.setMessage("Esta nota já existe, digite outra");
                alert.show();
            }else if (textoNota.length()>0) {
                etNota.setText("");
                etNota.findFocus();
                notas.add(textoNota);
                dbHelper.insert(textoNota);
                adapter.notifyDataSetChanged();
            }
            else if (textoNota.equals("")){
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Erro");
                alert.setMessage("Nota em branco nao pode ser adicionada");
                alert.show();
            }
        }
    });

    //Erro ao chamar o List View!!


//    listViewNotas.setOnClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
//            alert.setTitle("Atenção!");
//            alert.setMessage("Deseja apagar esta nota?");
//            final int posicaoRemover = position;
//            alert.setNegativeButton("Não",null);
//            alert.setPositiveButton("Sim", new AlertDialog.OnClickListener (){
//                @Override
//                public void onClick(DialogInterface dialog,int which){
//                    String textoNota = notas.get(posicaoRemover);
//                    notas.remove(posicaoRemover);
//                    dbHelper.delete(textoNota);
//                    adapter.notifyDataSetChanged();
//                }
//            });
//            alert.show();
//        }
//    });



        
    }
    private List<String> populateListViewFromDB(){
        List<String> notaStr = dbHelper.getFromDB();
        if (notaStr!=null){
            for (int i = 0; i< notaStr.size(); i++){
                etNota.findFocus();
                notas.add(notaStr.get(i));
                adapter.notifyDataSetChanged();
            }
        }
        return notaStr;
    }
    public boolean ExistNoteDB(String descricao){
        List<String> notaStr = dbHelper.getFromDB();
        boolean ret = false;
        if (notaStr != null && notaStr.size()>0){
            for (int i = 0; i < notaStr.size();i++){
                String notaDB = (String) notaStr.get(i);
                if (descricao.equals(notaDB))
                    return true;
            }
        }
        return ret;
    }
}