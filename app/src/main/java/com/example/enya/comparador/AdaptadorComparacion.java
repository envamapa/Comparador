package com.example.enya.comparador;

/**
 * Created by enya on 28/04/16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdaptadorComparacion extends BaseAdapter {

    private Activity actividad;
    private ArrayList<Producto> productos;

    public AdaptadorComparacion(Activity actividad, ArrayList<Producto> productos){
        this.actividad = actividad;
        this.productos = productos;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int position) {
        return productos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista = convertView;

        if(vista==null){
            LayoutInflater inflater = (LayoutInflater)actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vista = inflater.inflate(R.layout.elemento_ver_comparacion,null);
        }

        Producto producto = productos.get(position);

        TextView titulo = (TextView)vista.findViewById(R.id.tvTitulo);
        titulo.setText(producto.description);

        TextView upc = (TextView)vista.findViewById(R.id.tvUpc);
        upc.setText(producto.upc);

        TextView fecha = (TextView)vista.findViewById(R.id.tvFecha);
        fecha.setText(producto.fecha);

        return vista;
    }
}
