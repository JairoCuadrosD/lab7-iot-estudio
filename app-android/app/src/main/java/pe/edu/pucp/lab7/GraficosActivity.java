package pe.edu.pucp.lab7;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import pe.edu.pucp.lab7.api.AvanceResponse;
import pe.edu.pucp.lab7.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraficosActivity extends AppCompatActivity {

    private static final String TODOS = "Todos";

    private final List<Sesion> sesiones = new ArrayList<>();
    private Spinner spFiltro;
    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spFiltro = findViewById(R.id.spFiltro);
        chart = findViewById(R.id.chart);

        List<String> opciones = new ArrayList<>();
        opciones.add(TODOS);
        for (String c : Catalogo.CODIGOS) opciones.add(c);
        spFiltro.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, opciones));
        spFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { dibujar(); }
            @Override
            public void onNothingSelected(AdapterView<?> p) { }
        });

        findViewById(R.id.btnEvaluar).setOnClickListener(v -> evaluarAvance());

        cargarSesiones();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void cargarSesiones() {
        Db.sesiones().get().addOnSuccessListener(snap -> {
            sesiones.clear();
            for (DocumentSnapshot doc : snap.getDocuments()) {
                Sesion s = doc.toObject(Sesion.class);
                if (s != null) sesiones.add(s);
            }
            dibujar();
        });
    }

    private String filtro() {
        return spFiltro.getSelectedItem().toString();
    }

    /**
     * "Todos": una barra por curso (horas totales).
     * Un curso: una barra por fecha (horas de ese día).
     */
    private void dibujar() {
        String f = filtro();
        Map<String, Double> agrupado = new TreeMap<>(); // TreeMap = ordenado por clave
        for (Sesion s : sesiones) {
            if (!TODOS.equals(f) && !f.equals(s.getCurso())) continue;
            String clave = TODOS.equals(f) ? s.getCurso() : s.getFecha();
            agrupado.put(clave, agrupado.getOrDefault(clave, 0.0) + s.getHoras());
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Double> e : agrupado.entrySet()) {
            entries.add(new BarEntry(i++, e.getValue().floatValue()));
            etiquetas.add(e.getKey());
        }

        BarDataSet ds = new BarDataSet(entries, "Horas");
        ds.setColor(Color.parseColor("#1565C0"));
        ds.setValueTextSize(12f);
        chart.setData(new BarData(ds));

        XAxis x = chart.getXAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        x.setGranularity(1f);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("Sin sesiones");
        chart.invalidate(); // redibuja
    }

    private void evaluarAvance() {
        String codigo = filtro();
        if (TODOS.equals(codigo)) {
            Toast.makeText(this, "Seleccione un curso en el filtro", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = 0;
        for (Sesion s : sesiones) {
            if (codigo.equals(s.getCurso())) total += s.getHoras();
        }

        // enqueue() ejecuta la llamada en un hilo de fondo y devuelve el
        // resultado en el hilo principal -> no bloquea la UI
        RetrofitClient.api().evaluar(codigo, total).enqueue(new Callback<AvanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<AvanceResponse> call,
                                   @NonNull Response<AvanceResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(GraficosActivity.this, "Servicio no disponible", Toast.LENGTH_SHORT).show();
                    return;
                }
                mostrar(resp.body());
            }

            @Override
            public void onFailure(@NonNull Call<AvanceResponse> call, @NonNull Throwable t) {
                // Sin conexión, backend apagado, timeout... la app NO se cae
                Toast.makeText(GraficosActivity.this, "Servicio no disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrar(AvanceResponse r) {
        String msg = String.format(Locale.US,
                "Curso: %s%nHoras registradas: %.1f%nHoras recomendadas: %d%n%nEstado: %s",
                r.curso, r.horasRegistradas, r.horasRecomendadas, r.estado);
        new AlertDialog.Builder(this)
                .setTitle("Evaluar mi avance")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}
