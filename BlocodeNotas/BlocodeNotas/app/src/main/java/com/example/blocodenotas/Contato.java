package com.example.blocodenotas;

public class Contato {
    private String nome;
    private String endereco;
    private String empresa;

    Contato (String nome, String endereco, String empresa){
        this.nome = nome;
        this.endereco = endereco;
        this.empresa = empresa;
    }
    public String getNome(){return nome; }

    public String getEndereco(){ return endereco; }

    public String getEmpresa(){return empresa; }

    public void setEndereco(String endereco){this.endereco = endereco; }

    public void setEmpresa(String empresa) {this.empresa = empresa; }

    public void setNome(String nome){
        this.nome = nome;
    }


}
