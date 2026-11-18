package pe.edu.pucp.lab7;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/** Un solo lugar donde se arma la ruta de la colección del usuario. */
public final class Db {
    private Db() { }

    public static String uid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /** usuarios/{uid}/sesiones -> cada usuario solo ve lo suyo. */
    public static CollectionReference sesiones() {
        return FirebaseFirestore.getInstance()
                .collection("usuarios").document(uid())
                .collection("sesiones");
    }
}
