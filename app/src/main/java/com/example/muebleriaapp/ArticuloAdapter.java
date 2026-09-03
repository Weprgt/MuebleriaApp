package com.example.muebleriaapp;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArticuloAdapter
        extends RecyclerView.Adapter<ArticuloAdapter.ViewHolder> {

    public interface OnArticuloClickListener {
        void onArticuloClick(Articulo articulo);
    }

    private final Activity activity;
    private final ArrayList<Articulo> listaArticulos;
    private final OnArticuloClickListener listener;

    public ArticuloAdapter(
            Activity activity,
            ArrayList<Articulo> listaArticulos,
            OnArticuloClickListener listener
    ) {
        this.activity = activity;
        this.listaArticulos = listaArticulos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View vista = LayoutInflater.from(activity).inflate(
                R.layout.item_articulo,
                parent,
                false
        );

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Articulo articulo = listaArticulos.get(position);

        holder.txtNombre.setText(articulo.getNombre());
        holder.txtPrecio.setText("Q " + articulo.getPrecio());
        holder.txtCategoria.setText(articulo.getCategoria());
        holder.txtDescripcion.setText(articulo.getDescripcion());

        mostrarFotografia(
                holder.imgMiniatura,
                articulo.getFotoUri()
        );

        holder.itemView.setOnClickListener(v ->
                listener.onArticuloClick(articulo)
        );
    }

    @Override
    public int getItemCount() {
        return listaArticulos.size();
    }

    private void mostrarFotografia(
            ImageView imageView,
            String fotoUri
    ) {
        imageView.setImageTintList(null);
        imageView.setImageURI(null);
        imageView.setPadding(26, 26, 26, 26);
        imageView.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );
        imageView.setImageResource(R.drawable.ic_sofa);

        if (fotoUri == null || fotoUri.isEmpty()) {
            return;
        }

        try {
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );
            imageView.setImageURI(Uri.parse(fotoUri));

        } catch (Exception e) {
            imageView.setPadding(26, 26, 26, 26);
            imageView.setScaleType(
                    ImageView.ScaleType.CENTER_INSIDE
            );
            imageView.setImageResource(R.drawable.ic_sofa);
        }
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        final ImageView imgMiniatura;
        final TextView txtNombre;
        final TextView txtPrecio;
        final TextView txtCategoria;
        final TextView txtDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMiniatura =
                    itemView.findViewById(R.id.imgMiniatura);

            txtNombre =
                    itemView.findViewById(R.id.txtNombre);

            txtPrecio =
                    itemView.findViewById(R.id.txtPrecio);

            txtCategoria =
                    itemView.findViewById(R.id.txtCategoria);

            txtDescripcion =
                    itemView.findViewById(R.id.txtDescripcion);
        }
    }
}