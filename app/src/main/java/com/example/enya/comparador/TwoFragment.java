package com.example.enya.comparador;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class TwoFragment extends Fragment{

    AdaptadorComparacion adaptadorComparacion;

    int[] logos = {R.drawable.walmart,R.drawable.superama,R.drawable.cheadraui,R.drawable.comercial,R.drawable.citymarket,R.drawable.soriana};

    View view;

    SwipeRefreshLayout swipeLayout;

    ListView comparaciones;

    ArrayList<Producto> productosLista;

    private SQLiteDatabase baseDatos;
    private static final String TAG = "bdcomparaciones";
    private static final String nombreBD = "comparaciones";
    private static final String nombreTabla = "comparacion";

    private static final String crearTabla = "create table if not exists "
            + " comparacion (idComparacion integer primary key autoincrement, "
            + " upc varchar(10) not null, precio float not null, descripcion text not null, retailer text not null,"
            + " fecha text not null);";

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                comparaciones = (ListView) view.findViewById(R.id.comparaciones);

                final ArrayList<Producto> guardados = selectAllUpc();

                adaptadorComparacion = new AdaptadorComparacion(getActivity(), guardados);

                comparaciones.setAdapter(adaptadorComparacion);

                comparaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final Producto p = guardados.get(position);

                        Bundle args = new Bundle();
                        args.putString("upc", p.getUpc());
                        Fragment myFrag = new ThreeFragment();
                        myFrag.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_two, myFrag).addToBackStack(null).commit();

                    }
                });

                swipeLayout.setRefreshing(false);
            }
        });

        comparaciones = (ListView)view.findViewById(R.id.comparaciones);

        final ArrayList<Producto> guardados = selectAllUpc();

        adaptadorComparacion = new AdaptadorComparacion(getActivity(), guardados);

        comparaciones.setAdapter(adaptadorComparacion);

        comparaciones.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Producto p = guardados.get(position);
                confirmDialog(p.getUpc(), p.getFecha());
                return true;
            }
        });
        comparaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Producto p = guardados.get(position);

                Bundle args = new Bundle();
                args.putString("upc", p.getUpc());
                Fragment myFrag = new ThreeFragment();
                myFrag.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_two, myFrag).hide(TwoFragment.this).addToBackStack(null).commit();

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
                Cursor c1 = baseDatos.rawQuery("select descripcion from comparacion where upc="+p.getUpc(), null);
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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