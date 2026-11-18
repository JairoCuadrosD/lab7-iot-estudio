package pe.edu.pucp.lab7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;

/**
 * Pantalla de entrada. No tiene layout propio: FirebaseUI dibuja
 * iniciar sesión / registrarse / "¿olvidaste tu contraseña?".
 */
public class LoginActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> loginLauncher =
            registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this::onLoginResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si ya hay sesión guardada, entrar directo sin pedir credenciales
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            irALista();
            return;
        }

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                // EmailBuilder ya incluye registro y recuperación de contraseña
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();
        loginLauncher.launch(intent);
    }

    private void onLoginResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            irALista();
        } else {
            // El usuario retrocedió o hubo error: cerramos la app
            Toast.makeText(this, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void irALista() {
        startActivity(new Intent(this, ListaActivity.class));
        finish(); // para que "atrás" no vuelva al login
    }
}
