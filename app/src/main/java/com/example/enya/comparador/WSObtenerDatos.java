package com.example.enya.comparador;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

public class WSObtenerDatos {

    //ConexionServerPHP conexionIniciarSesion;
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT =  60 * 1000;
    public static final String SERVER_ADDRESS = "http://checkprices-spirit1.rhcloud.com/";
    public String nombreServicio = "";
    Context context;
    InputStream inputStream = null;
    String result = "";

    public WSObtenerDatos()
    {

        //conexionIniciarSesion = new ConexionServerPHP(context);
    }

    public void obtenerProductosInBackground(Producto producto, GetProductoCallback productoCallback){
        //conexionIniciarSesion.progressDialog.show();
        //progressDialog.show();
        new obtenerProductosAsyncTask(producto, productoCallback).execute();

    }

    public class obtenerProductosAsyncTask extends AsyncTask<Void, Void, ArrayList<Producto>>
        {
            Producto producto;
            GetProductoCallback productoCallback;

            public obtenerProductosAsyncTask(Producto producto, GetProductoCallback callback){
                this.producto = producto;
                this.productoCallback = callback;
            }


            @Override
            protected ArrayList<Producto> doInBackground(Void... params) {

                HttpParams httpRequestParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
                HttpClient client = new DefaultHttpClient(httpRequestParams);
                HttpGet post = new HttpGet(SERVER_ADDRESS + "loadProductFromAll?upc="+producto.getUpc());

                ArrayList<Producto> productos = new ArrayList<>();

                try{
                    HttpResponse httpResponse = client.execute(post);

                    HttpEntity entity = httpResponse.getEntity();
                    // Read content & Log
                    inputStream = entity.getContent();
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    StringBuilder sBuilder = new StringBuilder();

                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sBuilder.append(line + "\n");
                    }

                    inputStream.close();
                    result = sBuilder.toString();
                    System.out.println(result);
                    JSONArray jsonArray = new JSONArray(result);
                    System.out.println(jsonArray);




                    for(int i=0; i<jsonArray.length();i++){
                        Object obj = jsonArray.get(i);
                        System.out.println("OBJ:"+obj);
                        if(obj.toString() != "null")
                        {
                            JSONObject jproducto = jsonArray.getJSONObject(i);
                            System.out.println(jproducto);

                            String retailer = jproducto.getString("retailer");
                            String description = jproducto.getString("description");
                            BigDecimal precio = BigDecimal.valueOf(jproducto.getDouble("price"));
                            String upc = jproducto.getString("upc");

                            Producto producto = new Producto();
                            producto.setId(i+1);
                            producto.setRetailer(retailer);
                            producto.setDescription(description);
                            producto.setPrecio(precio);
                            producto.setUpc(upc);
                            productos.add(producto);
                        }else{
                            Producto producto = new Producto();
                            producto.setId(0);
                            productos.add(producto);
                        }
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }


                return productos;
            }

            @Override
            protected void onPostExecute(ArrayList<Producto> productos) {
                //progressDialog.dismiss();
                for(int i=0; i<productos.size(); i++) {
                    System.out.print("Producto" + productos);
                    productoCallback.done(productos.get(i));
                }
                super.onPostExecute(productos);
            }
        }


}
