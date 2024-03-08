package edu.spring.istfi.model;

import jakarta.persistence.*;

@Entity
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int codigo;
    private int codigoPostal;
    private String calle;
    private int numero;
    @OneToOne(mappedBy = "direccion")
    private Cliente cliente;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Direccion() {
    }

    public Direccion(int codigo, int codigoPostal, String calle, int numero) {
        this.codigo = codigo;
        this.codigoPostal = codigoPostal;
        this.calle = calle;
        this.numero = numero;
    }

    // Getter y Setter para código
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    // Getter y Setter para código postal
    public int getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    // Getter y Setter para calle
    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    // Getter y Setter para número
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Código: " + codigo + ", Código Postal: " + codigoPostal +
                ", Calle: " + calle + ", Número: " + numero;
    }
}
