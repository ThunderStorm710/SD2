package com.example.servingwebcontent.src.src;

import java.util.ArrayList;
import java.util.Map;

public class InfoGeral {

    private ArrayList<Storage> barrels;
    private ArrayList<DownloaderInfo> downloaders;
    private Map<String,Integer> mapaPesquisas;

    public InfoGeral(){}

    public InfoGeral(ArrayList<Storage> barrels, ArrayList<DownloaderInfo> downloaders, Map<String,Integer> mapaPesquisas){
        this.barrels = barrels;
        this.downloaders = downloaders;
        this.mapaPesquisas = mapaPesquisas;
    }

    public ArrayList<DownloaderInfo> getDownloaders() {
        return downloaders;
    }

    public ArrayList<Storage> getBarrels() {
        return barrels;
    }

    public Map<String, Integer> getMapaPesquisas() {
        return mapaPesquisas;
    }

    public void setMapaPesquisas(Map<String, Integer> mapaPesquisas) {
        this.mapaPesquisas = mapaPesquisas;
    }

    public void setBarrels(ArrayList<Storage> barrels) {
        this.barrels = barrels;
    }

    public void setDownloaders(ArrayList<DownloaderInfo> downloaders) {
        this.downloaders = downloaders;
    }
}
