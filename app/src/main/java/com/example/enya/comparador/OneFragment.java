package com.example.enya.comparador;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class OneFragment extends Fragment{

    Button verificar;
    Button guardar;

    ImageButton escanear;

    TextView nombre;

    EditText codigo;

    ListView resultados;

    ArrayList<Producto> productosLista;
    AdaptadorProductos adaptadorProductos;

    int[] logos = {R.drawable.walmart,R.drawable.superama,R.drawable.cheadraui,R.drawable.comercial,R.drawable.citymarket,R.drawable.soriana};

    View view;

    private SQLiteDatabase baseDatos;
    private static final String TAG = "bdcomparaciones";
    private static final String nombreBD = "comparaciones";
    private static final String nombreTabla = "comparacion";

    private static final String crearTabla = "create table if not exists "
            + " comparacion (idComparacion integer primary key autoincrement, "
            + " upc text not null, precio float not null, descripcion text not null, retailer text not null,"
            + " fecha text not null);";

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one, container, false);

        guardar = (Button)view.findViewById(R.id.guardar);
        guardar.setVisibility(View.INVISIBLE);

        nombre = (TextView)view.findViewById(R.id.NombreProducto);
        nombre.setVisibility(View.INVISIBLE);

        resultados = (ListView)view.findViewById(R.id.resultados);
        resultados.setVisibility(View.INVISIBLE);

        codigo = (EditText)view.findViewById(R.id.codigo);

        escanear = (ImageButton)view.findViewById(R.id.escanear);
        escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                scanIntegrator.initiateScan();
            }
        });

        verificar = (Button)view.findViewById(R.id.verificar);
        verificar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ArrayList<Producto> productos = new ArrayList<>();

                final String cb = codigo.getText().toString();
                Producto producto = new Producto(cb);
                WSObtenerDatos wsObtenerDatos = new WSObtenerDatos();
                wsObtenerDatos.obtenerProductosInBackground(producto, new GetProductoCallback() {
                    @Override
                    public void done(final Producto producto) {

                        nombre.setVisibility(View.VISIBLE);

                        if(producto.getPrecio().compareTo(BigDecimal.ZERO)!=0){
                            nombre.setText(producto.getDescription());

                            resultados.setVisibility(View.VISIBLE);

                            productos.add(producto);

                            productosLista = productos;

                            adaptadorProductos = new AdaptadorProductos(getActivity(), productosLista, logos);
                            resultados.setAdapter(adaptadorProductos);

                            guardar.setVisibility(View.VISIBLE);
                        }else{
                            nombre.setText("No se encontraron los precios del producto "+cb+" en "+producto.getRetailer());
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

                                    nombre.setVisibility(View.INVISIBLE);

                                    guardar.setVisibility(View.INVISIBLE);

                                    resultados.setVisibility(View.INVISIBLE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            codigo.setText(scanContent);
        }else{
            Toast toast = Toast.makeText(getActivity(),
                    "No se pudo realizar el escaneo", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Método que realiza la inserción de los datos en nuestra tabla contacto
    private boolean insertar(ArrayList<Producto> productos)
    {
        try{
            baseDatos = getActivity().openOrCreateDatabase(nombreBD, android.content.Context.MODE_PRIVATE, null);
            baseDatos.execSQL(crearTabla);
            ContentValues values = new ContentValues();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String fecha = sdf.format(c.getTime());
            System.out.println(fecha);
            for(int i=0; i<productos.size(); i++){
                Producto p = productos.get(i);
                values.put("upc",p.getUpc());
                values.put("precio", p.getPrecio().toString());
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
}