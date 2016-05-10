package com.example.enya.comparador;

import java.math.BigDecimal;

/**
 * Created by enya on 20/04/16.
 */
public class Producto {
    int id;
    String retailer;
    String description;
    BigDecimal precio;
    String upc;
    String fecha;

    public Producto(){

    }

    public Producto(String upc){
        this.upc = upc;
    }

    public Producto(int id, String retailer, String description, BigDecimal precio, String upc){
        this.id = id;
        this.retailer = retailer;
        this.description = description;
        this.precio = precio;
        this.upc = upc;
    }

    public Producto(int id, String retailer, String description, BigDecimal precio, String upc, String fecha){
        this.id = id;
        this.retailer = retailer;
        this.description = description;
        this.precio = precio;
        this.upc = upc;
        this.fecha = fecha;
    }

    public Producto(String upc, String fecha){
        this.upc = upc;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getUpc() {
        return upc;
    }

    public String getDescription() {
        return description;
    }

    public String getRetailer() {
        return retailer;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }
}
