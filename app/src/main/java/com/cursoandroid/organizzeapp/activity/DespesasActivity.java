package com.cursoandroid.organizzeapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroid.organizzeapp.R;
import com.cursoandroid.organizzeapp.config.ConfiguracaoFirebase;
import com.cursoandroid.organizzeapp.helper.Base64Custom;
import com.cursoandroid.organizzeapp.helper.DateUtil;
import com.cursoandroid.organizzeapp.model.Movimentacao;
import com.cursoandroid.organizzeapp.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private double despesaTotal, despesaAtualizada   ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoCategoria = findViewById(R.id.editCategoria2);
        campoDescricao = findViewById(R.id.editDescricao2);
        campoData = findViewById(R.id.editData2);
        campoValor = findViewById(R.id.editValorDespesa);

        //Preenche o campo data com a data atual
        campoData.setText(DateUtil.dataAtual());
        recuperarDespesaTotal();

    }

    public void salvarDespesa(View view){

        if(validarCamposDespesa()){

            double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao = new Movimentacao();
            movimentacao.setValor(Double.parseDouble(campoValor.getText().toString()));
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(campoData.getText().toString());
            movimentacao.setTipo("d");

            despesaAtualizada = despesaTotal+valorRecuperado;
            atualizarDespesa(despesaAtualizada);

            movimentacao.salvar(campoData.getText().toString());
            Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        }



    }

    public Boolean validarCamposDespesa(){
        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if(!textoValor.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoCategoria.isEmpty()){
                    if(!textoDescricao.isEmpty()){
                        return true;
                    }
                    else{
                        Toast.makeText(DespesasActivity.this,
                                "Descrição não foi preenchida",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }
                else{
                    Toast.makeText(DespesasActivity.this,
                            "Categoria não foi preenchida",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
            else{
                Toast.makeText(DespesasActivity.this,
                        "Data não foi preenchida",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else{
            Toast.makeText(DespesasActivity.this,
                    "Valor não foi preenchido",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarDespesaTotal(){
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesa(double despesa){
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);
    }

}