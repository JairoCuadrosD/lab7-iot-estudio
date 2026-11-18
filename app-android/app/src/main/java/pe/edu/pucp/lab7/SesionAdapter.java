package pe.edu.pucp.lab7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.VH> {

    public interface OnSesionClick {
        void onClick(Sesion sesion);
    }

    private final List<Sesion> datos = new ArrayList<>();
    private final OnSesionClick onClick;

    public SesionAdapter(OnSesionClick onClick) {
        this.onClick = onClick;
    }

    public void setDatos(List<Sesion> nuevos) {
        datos.clear();
        datos.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sesion, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Sesion s = datos.get(position);
        h.tvCurso.setText(s.getCurso() + " · " + s.getTipo());
        h.tvDetalle.setText(String.format(Locale.US, "%s — %.1f h", s.getFecha(), s.getHoras()));
        h.tvEvidencia.setVisibility(s.getEvidenciaUrl() != null ? View.VISIBLE : View.GONE);
        h.itemView.setOnClickListener(v -> onClick.onClick(s));
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvCurso, tvDetalle, tvEvidencia;

        VH(View v) {
            super(v);
            tvCurso = v.findViewById(R.id.tvCurso);
            tvDetalle = v.findViewById(R.id.tvDetalle);
            tvEvidencia = v.findViewById(R.id.tvEvidencia);
        }
    }
}
