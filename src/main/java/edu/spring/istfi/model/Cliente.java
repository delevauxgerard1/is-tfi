package edu.spring.istfi.model;

import jakarta.persistence.*;

@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int dni;
    private int cuit;
    private String Nombre;
    private String Apellido;
    private String razonSocial;
    @ManyToOne
    private Direccion direccion;

    // Constructor
    public Cliente(int id, int dni, int cuit, String razonSocial,String Nombre,String Apellido ,Direccion direccion) {
        this.id = id;
        this.dni = dni;
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.Nombre=Nombre;
        this.Apellido=Apellido;
        this.direccion = direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getdni() {
        return dni;
    }

    public void setdni(int dni) {
        this.dni = dni;
    }

    public int getcuit() {
        return cuit;
    }

    public void setcuit(int cuit) {
        this.cuit = cuit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}
