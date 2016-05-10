package com.example.enya.comparador;

/**
 * Created by enya on 28/04/16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class AdaptadorProductos extends BaseAdapter {

    private Activity actividad;
    private ArrayList<Producto> productos;
    private int[] imagen;

    public AdaptadorProductos(Activity actividad, ArrayList<Producto> productos, int[] imagen){
        this.actividad = actividad;
        this.productos = productos;
        this.imagen = imagen;
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
            vista = inflater.inflate(R.layout.elemento_ver_productos,null);
        }

        Producto producto = productos.get(position);

        ImageView logo = (ImageView)vista.findViewById(R.id.imageView1);
        if(producto.getRetailer().equals("Walmart"))
            logo.setImageResource(R.drawable.walmart);
        if(producto.getRetailer().equals("Soriana"))
            logo.setImageResource(R.drawable.soriana);
        if(producto.getRetailer().equals("Chedraui"))
            logo.setImageResource(R.drawable.cheadraui);
        if(producto.getRetailer().equals("LaComer"))
            logo.setImageResource(R.drawable.comercial);
        if(producto.getRetailer().equals("Superama"))
            logo.setImageResource(R.drawable.superama);
        if(producto.getRetailer().equals("CityMarket"))
            logo.setImageResource(R.drawable.citymarket);

        TextView titulo = (TextView)vista.findViewById(R.id.tvTitulo);
        titulo.setText(NumberFormat.getCurrencyInstance().format(producto.precio));

        TextView fecha = (TextView)vista.findViewById(R.id.tvFecha);
        fecha.setText(producto.description);
        return vista;
    }
}
