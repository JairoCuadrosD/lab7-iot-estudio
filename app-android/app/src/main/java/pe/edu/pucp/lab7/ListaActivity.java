package pe.edu.pucp.lab7;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private SesionAdapter adapter;
    private TextView tvVacio;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        tvVacio = findViewById(R.id.tvVacio);
        RecyclerView rv = findViewById(R.id.rvSesiones);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Click en una fila -> editar esa sesión
        adapter = new SesionAdapter(sesion -> {
            Intent i = new Intent(this, FormSesionActivity.class);
            i.putExtra(FormSesionActivity.EXTRA_ID, sesion.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAgregar);
        fab.setOnClickListener(v -> startActivity(new Intent(this, FormSesionActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Listener en tiempo real: se dispara cada vez que cambia la colección
        listener = Db.sesiones()
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, error) -> {
                    if (error != null || snap == null) {
                        Toast.makeText(this, "Error al leer sesiones", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Sesion> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Sesion s = doc.toObject(Sesion.class);
                        if (s != null) {
                            s.setId(doc.getId());
                            lista.add(s);
                        }
                    }
                    adapter.setDatos(lista);
                    tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Importante: quitar el listener para no seguir escuchando con la pantalla cerrada
        if (listener != null) listener.remove();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuGraficos) {
            startActivity(new Intent(this, GraficosActivity.class));
            return true;
        } else if (id == R.id.menuCerrarSesion) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(t -> {
                Intent i = new Intent(this, LoginActivity.class);
                // Limpia la pila para que "atrás" no regrese a la lista
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
