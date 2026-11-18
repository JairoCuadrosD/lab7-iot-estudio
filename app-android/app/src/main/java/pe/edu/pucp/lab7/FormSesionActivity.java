package pe.edu.pucp.lab7;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/** Sirve para crear (sin EXTRA_ID) y para editar (con EXTRA_ID). */
public class FormSesionActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "idSesion";

    private EditText etCurso, etFecha, etHoras;
    private Spinner spTipo;
    private ImageView ivEvidencia;
    private Button btnGuardar, btnEliminar;
    private ProgressBar progress;

    private String idSesion;            // null = creando
    private String evidenciaUrlActual;  // la que ya tenía la sesión (al editar)
    private Uri imagenNueva;            // la elegida en galería (si hay)

    // Abre la galería y devuelve el Uri de la imagen elegida
    private final ActivityResultLauncher<String> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imagenNueva = uri;
                    ivEvidencia.setImageURI(uri); // vista previa local
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_sesion);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etCurso = findViewById(R.id.etCurso);
        etFecha = findViewById(R.id.etFecha);
        etHoras = findViewById(R.id.etHoras);
        spTipo = findViewById(R.id.spTipo);
        ivEvidencia = findViewById(R.id.ivEvidencia);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        progress = findViewById(R.id.progress);

        spTipo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Catalogo.TIPOS));

        etFecha.setOnClickListener(v -> elegirFecha());
        findViewById(R.id.btnAdjuntar).setOnClickListener(v -> galeriaLauncher.launch("image/*"));
        btnGuardar.setOnClickListener(v -> guardar());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());

        idSesion = getIntent().getStringExtra(EXTRA_ID);
        if (idSesion != null) {
            btnEliminar.setVisibility(View.VISIBLE);
            cargarSesion();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void elegirFecha() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (dp, y, m, d) ->
                etFecha.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cargarSesion() {
        Db.sesiones().document(idSesion).get().addOnSuccessListener(doc -> {
            Sesion s = doc.toObject(Sesion.class);
            if (s == null) return;
            etCurso.setText(s.getCurso());
            etFecha.setText(s.getFecha());
            etHoras.setText(String.valueOf(s.getHoras()));
            int pos = Arrays.asList(Catalogo.TIPOS).indexOf(s.getTipo());
            if (pos >= 0) spTipo.setSelection(pos);

            // Guardamos la URL para no perderla si el usuario no cambia la foto
            evidenciaUrlActual = s.getEvidenciaUrl();
            if (evidenciaUrlActual != null) {
                Glide.with(this).load(evidenciaUrlActual).into(ivEvidencia);
            }
        });
    }

    private void guardar() {
        String curso = etCurso.getText().toString().trim().toUpperCase();
        String fecha = etFecha.getText().toString().trim();
        String horasTxt = etHoras.getText().toString().trim();

        if (!Catalogo.esValido(curso)) {
            etCurso.setError("Código no válido (1TEL02..1TEL05)");
            return;
        }
        if (fecha.isEmpty()) {
            etFecha.setError("Elija una fecha");
            return;
        }
        double horas;
        try {
            horas = Double.parseDouble(horasTxt);
        } catch (NumberFormatException e) {
            etHoras.setError("Número inválido");
            return;
        }
        if (horas <= 0) {
            etHoras.setError("Debe ser mayor a 0");
            return;
        }

        Sesion s = new Sesion();
        s.setCurso(curso);
        s.setFecha(fecha);
        s.setHoras(horas);
        s.setTipo(spTipo.getSelectedItem().toString());
        s.setEvidenciaUrl(evidenciaUrlActual); // se conserva la anterior por defecto

        // Si es nueva, pedimos un id ANTES de guardar: lo necesitamos para la ruta en Storage
        DocumentReference ref = idSesion == null
                ? Db.sesiones().document()
                : Db.sesiones().document(idSesion);

        cargando(true);
        if (imagenNueva == null) {
            escribir(ref, s);
        } else {
            subirImagenYGuardar(ref, s);
        }
    }

    /** evidencias/{uid}/{idSesion}.jpg -> getDownloadUrl -> guardar en evidenciaUrl */
    private void subirImagenYGuardar(DocumentReference ref, Sesion s) {
        StorageReference img = FirebaseStorage.getInstance().getReference()
                .child("evidencias/" + Db.uid() + "/" + ref.getId() + ".jpg");

        img.putFile(imagenNueva)
                // continueWithTask encadena: cuando termina la subida, pide la URL
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return img.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    s.setEvidenciaUrl(uri.toString());
                    escribir(ref, s);
                })
                .addOnFailureListener(e -> {
                    cargando(false);
                    Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void escribir(DocumentReference ref, Sesion s) {
        ref.set(s)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Sesión guardada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    cargando(false);
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar sesión")
                .setMessage("¿Seguro que desea eliminarla?")
                .setPositiveButton("Eliminar", (d, w) -> eliminar())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminar() {
        cargando(true);
        Db.sesiones().document(idSesion).delete()
                .addOnSuccessListener(v -> {
                    // Borrar también la foto en Storage (si no existe, se ignora el error)
                    if (evidenciaUrlActual != null) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(evidenciaUrlActual).delete();
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    cargando(false);
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargando(boolean si) {
        progress.setVisibility(si ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!si);
        btnEliminar.setEnabled(!si);
    }
}
