package com.example.servingwebcontent.src.src;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;


public class SearchModule extends UnicastRemoteObject implements SearchModule_I, Runnable {

    private ArrayList<ClienteInfo> clientes;
    private ArrayList<Storage> barrels;
    private ArrayList<DownloaderInfo> downloaders;
    transient Thread t;

    private final String MULTICAST_ADDRESS = "224.3.2.3";
    private final int PORT = 1234;

    public SearchModule() throws RemoteException {
        super();
        this.clientes = new ArrayList<>();
        this.barrels = new ArrayList<>();
        this.downloaders = new ArrayList<>();
        t = new Thread(this);
        t.start();
    }

    public void verificarBarrels() {
        Runnable runnable = this::verificarBarrelsFuncao;
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void verificarDownloaders() {
        Runnable runnable = this::verificarDownloadersFuncao;
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void verificarBarrelsFuncao() {
        int i;
        Duration diff;

        try {
            while (true) {
                synchronized (barrels) {
                    i = 0;
                    while (i < barrels.size()) {
                        diff = Duration.between(barrels.get(i).getTempo(), LocalTime.now());
                        if (diff.getSeconds() > 5) {
                            //System.out.println("REMOVI " + barrels.get(i));
                            atualizarInfo("-", barrels.get(i), null);
                            barrels.remove(i);
                        }
                    }
                    Thread.sleep(3000);
                }

            }

        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }

    public void verificarDownloadersFuncao() {
        Duration diff;
        int i;
        try {
            while (true) {
                synchronized (downloaders) {
                    i = 0;
                    while (i < downloaders.size()) {
                        diff = Duration.between(downloaders.get(i).getTempo(), LocalTime.now());
                        if (diff.getSeconds() > 5) {
                            //System.out.println("REMOVI " + downloaders.get(i));
                            atualizarInfo("-", null, downloaders.get(i));
                            downloaders.remove(i);
                        }
                        i++;
                    }
                    Thread.sleep(3000);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }

    public void run() {

        MulticastSocket socket;
        DatagramPacket packet;
        String[] linha;
        String MULTICAST_ADDRESS = "224.3.2.2";
        String message;
        int PORT = 4322;
        boolean flag;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[254];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                message = new String(packet.getData(), 0, packet.getLength());
                linha = message.split("\\|");

                switch (linha[0]) {
                    case "10" -> {
                        flag = true;
                        if (linha.length == 4) {
                            for (DownloaderInfo d : downloaders) {
                                if (d.getIp().equals(linha[2]) && d.getPorto().equals(linha[3])) {
                                    d.setTempo(LocalTime.now());
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                downloaders.add(new DownloaderInfo(linha[1], linha[2], linha[3]));
                                atualizarInfo("+", null, downloaders.get(downloaders.size() - 1));

                            }
                        }
                    }
                    case "20" -> {
                        flag = true;
                        if (linha.length == 4) {
                            for (Storage s : barrels) {
                                if (s.getIp().equals(linha[1]) && s.getPorto().equals(linha[2]) && s.getGama().equals(linha[3])) {
                                    s.setTempo(LocalTime.now());
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                barrels.add(new Storage(linha[3], linha[1], linha[2]));
                                atualizarInfo("+", barrels.get(barrels.size() - 1), null);
                            }
                        }
                    }
                    default -> {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    synchronized public boolean indexarURL(ClienteInfo cliente, String url) throws RemoteException {
        boolean flag = false;
        if (verificarCliente(cliente)) {
            try {
                FilaURL_I h = (FilaURL_I) LocateRegistry.getRegistry(1099).lookup("fila_url");
                System.out.println("Vou adicionar o url " + url + " a fila!");
                if (h.recUrl(url)) {
                    flag = true;
                }

            } catch (Exception e) {
                System.out.println("Error" + e);
            }

        }
        return flag;
    }

    public void indexarALista(ClienteInfo cliente, String url) throws RemoteException {
        if (verificarCliente(cliente)) {
            try {
                FilaURL_I h = (FilaURL_I) LocateRegistry.getRegistry(1099).lookup("fila_url");
                System.out.println("Vou adicionar o url " + url + " a fila!");
                h.adicionarAForca(url);

            } catch (Exception e) {
                System.out.println("Error" + e);
            }

        }
    }

    public HashMap<String, HashSet<String[]>> obterInfoFicheiros(String gama, String ip, String porto) throws RemoteException {
        try {
            for (Storage s : barrels) {
                if ((!s.getIp().equals(ip) || !s.getPorto().equals(porto)) && s.getGama().equals(gama)) {
                    StorageBarrel_I b = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(porto)).lookup("Storage_Barrel");
                    return b.obterIndex();
                }

            }
        } catch (NotBoundException e) {
            System.out.println("Erro: " + e);
        }

        return null;
    }

    public HashMap<String, HashSet<String>> obterURLFicheiros(String gama, String ip, String porto) throws RemoteException {
        try {
            for (Storage s : barrels) {
                if ((!s.getIp().equals(ip) || !s.getPorto().equals(porto)) && s.getGama().equals(gama)) {
                    StorageBarrel_I b = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(porto)).lookup("Storage_Barrel");
                    return b.obterURLMap();
                }
            }
        } catch (NotBoundException e) {
            System.out.println("Erro: " + e);
        }

        return null;
    }

    public HashMap<String, Integer> pesquisasFrequentes() throws RemoteException {
        HashMap<String, Integer> mapa = new HashMap<>(), aux;
        try {
            for (Storage s : barrels) {
                StorageBarrel_I b = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(s.getPorto())).lookup("Storage_Barrel");
                aux = b.obterPesquisas();
                for (String cadeia : aux.keySet()) {
                    if (mapa.containsKey(cadeia)) {
                        mapa.put(cadeia, aux.get(cadeia) + 1);
                    } else {
                        mapa.put(cadeia, aux.get(cadeia));

                    }
                }
            }
        } catch (NotBoundException e) {
            System.out.println("Erro: " + e);
        }

        return mapa;
    }


    synchronized public HashSet<String[]> pesquisarPaginas(ClienteInfo cliente, String pesquisa) throws RemoteException {
        ArrayList<HashSet<String[]>> lista = new ArrayList<>();
        HashMap<String, Integer> mapaFreqs = new HashMap<>();
        HashSet<String[]> aux;
        int porto, freq;
        boolean flag = true;


        if (verificarCliente(cliente)) {
            try {
                String[] palavras = pesquisa.split(" ");
                StorageBarrel_I sI;
                for (String palavra : palavras) {
                    for (Storage s : barrels) {
                        if (flag) {
                            sI = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(s.getPorto())).lookup("Storage_Barrel");
                            sI.adicionarPesquisa(pesquisa);
                            flag = false;

                        }
                        if (Character.toUpperCase(palavra.charAt(0)) >= s.getGama().charAt(1) && Character.toUpperCase(palavra.charAt(0)) <= s.getGama().charAt(3)) {
                            porto = Integer.parseInt(s.getPorto());
                            sI = (StorageBarrel_I) LocateRegistry.getRegistry(porto).lookup("Storage_Barrel");
                            aux = sI.obterInfoBarrel(palavra);
                            if (aux != null){
                                lista.add(aux);
                            }
                            break;
                        }

                    }
                    System.out.println(palavra);

                }
                if (lista.isEmpty()){
                    return null;
                }
                HashSet<String[]> set = intersection(lista);
                HashSet<String> aux1;


                for (Storage s : barrels) {
                    porto = Integer.parseInt(s.getPorto());
                    sI = (StorageBarrel_I) LocateRegistry.getRegistry(porto).lookup("Storage_Barrel");
                    for (String[] link : set) {
                        aux1 = sI.obterLinks(link[0]);
                        if (aux1 != null) {
                            freq = aux1.size();
                            mapaFreqs.put(link[0], freq);
                        } else {
                            mapaFreqs.put(link[0], 0);
                        }

                    }
                    break;
                }

                Comparator<String[]> comparador = (o1, o2) -> {
                    int ocorrenciasO1 = mapaFreqs.get(o1[0]);
                    int ocorrenciasO2 = mapaFreqs.get(o2[0]);
                    return Integer.compare(ocorrenciasO2, ocorrenciasO1); // Ordenar em ordem decrescente
                };

                ArrayList<String[]> listaOrdenada = new ArrayList<>(set);
                listaOrdenada.sort(comparador);

                return set;

            } catch (Exception e) {
                System.out.println("Error" + e);
                e.printStackTrace();
            }

        }
        return null;
    }

    public static HashSet<String[]> intersection(ArrayList<HashSet<String[]>> sets) {
        if (sets == null){
            return null;
        }
        HashSet<String> allLinks = new HashSet<>();
        for (String[] set : sets.get(0)) {
            allLinks.add(set[0]);
        }

        for (int i = 1; i < sets.size(); i++) {
            HashSet<String[]> currentSet = sets.get(i);
            HashSet<String> currentLinks = new HashSet<>();
            for (String[] set : currentSet) {
                currentLinks.add(set[0]);
            }
            allLinks.retainAll(currentLinks);
        }

        HashSet<String[]> result = new HashSet<>();

        for (String[] set : sets.get(0)) {
            if (allLinks.contains(set[0])) {
                result.add(set);
            }
        }

        return result;
    }

    synchronized public boolean verificarCliente(ClienteInfo cliente) {

        for (ClienteInfo c : clientes) {
            if (cliente.getNome().equals(c.getNome())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ClienteInfo> obterClientes(String gama, String ip, String porto) throws RemoteException {
        ArrayList<ClienteInfo> clienteInfos = null;
        try {
            for (Storage b : barrels) {
                if (!b.getGama().equals(gama) || !b.getPorto().equals(porto) || !b.getIp().equals(ip)) {
                    StorageBarrel_I sI = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(porto)).lookup("Storage_Barrel");
                    clienteInfos = sI.obterClientesBarrel();
                    break;
                }

            }
        } catch (NotBoundException e) {
            System.out.println("Erro: " + e);
        }
        return clienteInfos;
    }

    public void atualizarInfo(String flag, Storage s, DownloaderInfo d) {
        try {
            MulticastSocket socket;
            String pacote = "";
            if (flag.equals("+")) {
                pacote += "Novo ";
            } else if (flag.equals("-")) {
                pacote += "Fechado ";
            }

            if (s != null) {
                pacote += s;
            } else if (d != null) {
                pacote += d;
            }

            byte[] buffer = pacote.getBytes();
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            System.out.println(pacote + "---------");
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClienteInfo verificarRegisto(String nome, String email, String username, String password, int porto) throws RemoteException {
        boolean flag = true;
        ClienteInfo c1 = null;
        System.out.println(nome + "--" + email + "--" + username + "-- " + password);
        try {
            if (barrels.size() != 0) {
                StorageBarrel_I sI = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(barrels.get(0).getPorto())).lookup("Storage_Barrel");
                clientes = sI.obterClientesBarrel();
            }
            if (clientes != null) {
                for (ClienteInfo c : clientes) {
                    if (c.getNome().equals(nome) || c.getEmail().equals(email) || c.getUsername().equals(username)) {
                        System.out.println(c.getNome() + "--" + c.getEmail() + "--" + c.getUsername() + "-- " + password);

                        System.out.println("[REGISTO] Nome, username ou email ja se encontram associados a um utilizador ja existente na base de dados...por favor volte a inserir as suas credencias...");
                        flag = false;
                    }

                }
            } else {
                clientes = new ArrayList<>();
            }
            if (flag) {

                c1 = new ClienteInfo(nome, username, email, password, porto);
                clientes.add(c1);
                for (Storage s : barrels) {
                    System.out.println("[REGISTO] Cliente adicionado aos Barrels");
                    StorageBarrel_I sI = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(s.getPorto())).lookup("Storage_Barrel");
                    sI.adicionarCliente(c1);
                }
                System.out.println("Inscricao validada com sucesso!");
                System.out.println("Seja bem-vindo, " + nome + "!!\n");

            }
        } catch (NotBoundException e) {
            System.out.println("Erro: " + e);
        }
        return c1;
    }

    public ClienteInfo verificarLogin(String username, String password) throws RemoteException {
        ClienteInfo c1 = null;
        try {
            if (barrels.size() != 0) {
                StorageBarrel_I sI = (StorageBarrel_I) LocateRegistry.getRegistry(Integer.parseInt(barrels.get(0).getPorto())).lookup("Storage_Barrel");
                clientes = sI.obterClientesBarrel();
                for (ClienteInfo c : clientes) {
                    if (c.getUsername().equals(username) && c.getPassword().equals(password)) {
                        c1 = c;
                    }
                }
            }
        } catch (NotBoundException e) {
            System.out.println("Erro: " + e);
        }

        return c1;
    }

    public boolean adicionarInfoInicialBarrel(String gama, String ip, String porto) throws RemoteException {
        boolean flag = true;
        if (gama.length() != 5) {
            flag = false;
        } else if ((Character.toUpperCase(gama.charAt(0)) == 'A' && Character.toUpperCase(gama.charAt(3)) != 'M') || (Character.toUpperCase(gama.charAt(0)) == 'N' && Character.toUpperCase(gama.charAt(3)) != 'Z')) {
            flag = false;
        } else {
            if (barrels != null) {
                for (Storage s : barrels) {
                    if (s.getPorto().equals(porto) && s.getIp().equals(ip)) {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                barrels = new ArrayList<>();
                barrels.add(new Storage(gama, ip, porto));
            }
        }
        return flag;
    }

    public ArrayList<HashSet<String>> obterLinks(ClienteInfo cliente, String url) throws RemoteException {
        ArrayList<HashSet<String>> lista = new ArrayList<>();
        HashSet<String> aux;
        try {
            if (verificarCliente(cliente)) {
                System.out.println("--- PESQUISA ---");
                System.out.println(barrels);
                for (Storage s : barrels) {
                    int porto = Integer.parseInt(s.getPorto());
                    StorageBarrel_I sI = (StorageBarrel_I) LocateRegistry.getRegistry(porto).lookup("Storage_Barrel");
                    if ((aux = sI.obterLinks(url)) != null) {
                        lista.add(aux);
                    }
                }
                System.out.println(lista);
                System.out.println("--- FIM PESQUISA ---");
            } else {
                System.out.println("Permissoes insuficientes...");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e);
        }
        return lista;
    }

    public ArrayList<Storage> obterInfoBarrels() throws RemoteException {
        return barrels;
    }

    public ArrayList<DownloaderInfo> obterInfoDownloaders() throws RemoteException {
        return downloaders;
    }


    public static void main(String[] args) {
        try {
            SearchModule sm1 = new SearchModule();

            Registry r = LocateRegistry.createRegistry(1100);
            r.rebind("Search_Module", sm1);
            sm1.verificarBarrels();
            sm1.verificarDownloaders();

            sm1.t.join();


        } catch (Exception e) {
            System.out.println("Error" + e);
        }
    }


}