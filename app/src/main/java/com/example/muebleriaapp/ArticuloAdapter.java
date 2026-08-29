package com.example.muebleriaapp;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArticuloAdapter extends ArrayAdapter<Articulo> {

    private final Activity activity;
    private final ArrayList<Articulo> listaArticulos;

    public ArticuloAdapter(
            Activity activity,
            ArrayList<Articulo> listaArticulos
    ) {
        super(activity, R.layout.item_articulo, listaArticulos);

        this.activity = activity;
        this.listaArticulos = listaArticulos;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();

            convertView = inflater.inflate(
                    R.layout.item_articulo,
                    parent,
                    false
            );

            holder = new ViewHolder();

            holder.imgMiniatura =
                    convertView.findViewById(R.id.imgMiniatura);

            holder.txtNombre =
                    convertView.findViewById(R.id.txtNombre);

            holder.txtPrecio =
                    convertView.findViewById(R.id.txtPrecio);

            holder.txtCategoria =
                    convertView.findViewById(R.id.txtCategoria);

            holder.txtDescripcion =
                    convertView.findViewById(R.id.txtDescripcion);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Articulo articulo = listaArticulos.get(position);

        holder.txtNombre.setText(articulo.getNombre());
        holder.txtPrecio.setText("Q " + articulo.getPrecio());
        holder.txtCategoria.setText(articulo.getCategoria());
        holder.txtDescripcion.setText(articulo.getDescripcion());

        mostrarFotografia(holder.imgMiniatura, articulo.getFotoUri());

        return convertView;
    }

    private void mostrarFotografia(
            ImageView imageView,
            String fotoUri
    ) {
        // Restablecer la vista antes de mostrar el artículo.
        imageView.setImageTintList(null);
        imageView.setImageURI(null);
        imageView.setPadding(26, 26, 26, 26);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.ic_sofa);

        if (fotoUri != null && !fotoUri.isEmpty()) {
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
    }

    private static class ViewHolder {
        ImageView imgMiniatura;
        TextView txtNombre;
        TextView txtPrecio;
        TextView txtCategoria;
        TextView txtDescripcion;
    }
}