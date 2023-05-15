package com.example.servingwebcontent.forms;

import java.io.Serializable;

public class Pesquisa implements Serializable {

    private String textoPesquisa;

    public Pesquisa() {
    }

    public Pesquisa(String pesquisa) {
        this.textoPesquisa = pesquisa;
    }

    public String toString() {
        return "Pesquisa:" + textoPesquisa;
    }

    public void setTextoPesquisa(String textoPesquisa) {
        this.textoPesquisa = textoPesquisa;
    }

    public String getTextoPesquisa() {
        return textoPesquisa;
    }
}
