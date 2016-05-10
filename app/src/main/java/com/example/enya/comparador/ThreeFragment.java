package com.example.enya.comparador;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ThreeFragment extends Fragment{

    int[] logos = {R.drawable.walmart,R.drawable.superama,R.drawable.cheadraui,R.drawable.comercial,R.drawable.citymarket,R.drawable.soriana};

    AdaptadorProductos adaptadorProductos;

    View view;

    ListView resultado;

    TextView tvFecha;
    TextView tvNombre;

    Button verificar;
    Button guardar;
    Button regresar;

    ArrayList<Producto> productosLista;

    private SQLiteDatabase baseDatos;
    private static final String TAG = "bdcomparaciones";
    private static final String nombreBD = "comparaciones";
    private static final String nombreTabla = "comparacion";

    private static final String crearTabla = "create table if not exists "
            + " comparacion (idComparacion integer primary key autoincrement, "
            + " upc varchar(10) not null, precio float not null, descripcion text not null, retailer text not null,"
            + " fecha text not null);";

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_three, container, false);

        regresar = (Button) view.findViewById(R.id.regresar);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        resultado = (ListView)view.findViewById(R.id.resultados);

        tvFecha = (TextView)view.findViewById(R.id.tvFecha);
        tvNombre = (TextView)view.findViewById(R.id.NombreProducto);

        verificar = (Button)view.findViewById(R.id.verificar);
        guardar = (Button)view.findViewById(R.id.guardar);

        Bundle bundle = getArguments();
        final Producto p = new Producto();
        p.setUpc(bundle.getString("upc"));

        tvNombre.setText(p.getUpc());

        ArrayList<Producto> compguardada = selectAll(p.getUpc(), p.getFecha());

        tvFecha.setText(tvFecha.getText() + p.getFecha());
        adaptadorProductos = new AdaptadorProductos(getActivity(), compguardada, logos);

        resultado.setAdapter(adaptadorProductos);

        verificar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ArrayList<Producto> productos = new ArrayList<>();

                Producto producto = new Producto(p.getUpc());
                WSObtenerDatos wsObtenerDatos = new WSObtenerDatos();
                wsObtenerDatos.obtenerProductosInBackground(producto, new GetProductoCallback() {
                    @Override
                    public void done(final Producto producto) {

                        tvNombre.setVisibility(View.VISIBLE);

                        if (producto.getPrecio().compareTo(BigDecimal.ZERO) != 0) {
                            tvNombre.setText(producto.getDescription());

                            resultado.setVisibility(View.VISIBLE);

                            productos.add(producto);

                            productosLista = productos;

                            adaptadorProductos = new AdaptadorProductos(getActivity(), productosLista, logos);
                            resultado.setAdapter(adaptadorProductos);

                            guardar.setVisibility(View.VISIBLE);
                        } else {
                            tvNombre.setText("No se encontraron los precios del producto " + p.getUpc());
                        }
                        guardar.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                if (insertar(productos)) {
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    dialogBuilder.setMessage("La comparación del producto se ha guardado exitósamente")
                                            .setTitle("Comparación guardada")
                                            .setPositiveButton("Aceptar", null);
                                    dialogBuilder.show();

                                    /*tvNombre.setVisibility(View.INVISIBLE);
                                    guardar.setVisibility(View.INVISIBLE);
                                    resultado.setVisibility(View.INVISIBLE);
                                    comparaciones.setVisibility(View.VISIBLE);
                                    tvFecha.setVisibility(View.INVISIBLE);
                                    verificar.setVisibility(View.INVISIBLE);*/

                                } else {
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    dialogBuilder.setMessage("La comparación del producto no ha sido guardada")
                                            .setTitle("Error")
                                            .setPositiveButton("Aceptar", null);
                                    dialogBuilder.show();
                                }

                            }
                        });
                    }
                });

            }

        });

        return view;
    }

    private ArrayList<Producto> selectAllUpc(){
        try{
            baseDatos = getActivity().openOrCreateDatabase(nombreBD, android.content.Context.MODE_PRIVATE, null);
            baseDatos.execSQL(crearTabla);
        }
        catch (Exception e){
            Log.i(TAG, "Error al abrir o crear la base de datos" + e);
        }

        ArrayList<Producto> productos = new ArrayList();

        Cursor c = baseDatos.rawQuery("SELECT distinct upc, fecha FROM comparacion", null);

        if (c.moveToFirst()) {
            do {
                Producto p = new Producto();
                p.setUpc(c.getString(0));
                p.setFecha(c.getString(1));
                Cursor c1 = baseDatos.rawQuery("select descripcion from comparacion where upc="+p.getUpc()+" and fecha='"+p.getFecha()+"'", null);
                c1.moveToLast();
                p.setDescription(c1.getString(0));
                productos.add(p);
            } while(c.moveToNext());
        }

        return productos;
    }

    private ArrayList<Producto> selectAll(String upc, String fecha){
        try{
            baseDatos = getActivity().openOrCreateDatabase(nombreBD, android.content.Context.MODE_PRIVATE, null);
            baseDatos.execSQL(crearTabla);
        }
        catch (Exception e){
            Log.i(TAG, "Error al abrir o crear la base de datos" + e);
        }

        ArrayList<Producto> productos = new ArrayList();

        System.out.print(fecha);

        Cursor c = baseDatos.rawQuery("SELECT * FROM comparacion where upc="+upc+" and fecha='"+fecha+"'", null);

        if (c.moveToFirst()) {
            do {
                Producto p = new Producto();
                p.setUpc(c.getString(1));
                p.setPrecio(BigDecimal.valueOf(c.getFloat(2)));
                p.setDescription(c.getString(3));
                p.setRetailer(c.getString(4));
                p.setFecha(c.getString(5));
                productos.add(p);
            } while(c.moveToNext());
        }

        return productos;
    }

    private boolean insertar(ArrayList<Producto> productos)
    {
        try{
            baseDatos = getActivity().openOrCreateDatabase(nombreBD, android.content.Context.MODE_PRIVATE, null);
            baseDatos.execSQL(crearTabla);
            ContentValues values = new ContentValues();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String fecha = sdf.format(c.getTime());
            for(int i=0; i<productos.size(); i++){
                Producto p = productos.get(i);
                values.put("upc",p.getUpc());
                values.put("precio",p.getPrecio().toString());
                values.put("descripcion",p.getDescription());
                values.put("retailer", p.getRetailer());
                values.put("fecha",fecha);
                baseDatos.insert(nombreTabla, null, values);
            }
            return true;
        }
        catch (Exception e) {
            Log.i(TAG, "Error al abrir o crear la base de datos" + e);
            return false;
        }
    }

    private void confirmDialog(final String upc, final String fecha) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder
                .setMessage("¿Seguro que desea eliminar esta comparación?")
                .setPositiveButton("Aceptar",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String[] args = {upc, fecha};
                        baseDatos.delete(nombreTabla,"upc=? and fecha=?",args);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}