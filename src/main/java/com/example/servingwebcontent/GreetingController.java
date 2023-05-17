package com.example.servingwebcontent;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.example.servingwebcontent.forms.Pesquisa;
import com.example.servingwebcontent.src.src.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class GreetingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private ClienteInfo cliente;
    private SearchModule_I h;

    @GetMapping("/")
    public String page() {
        if (cliente == null) {
            return "redirect:/login";
        } else {
            return "page";
        }
    }

    @GetMapping("/indexar")
    public String indexar(@RequestParam(name = "valor", defaultValue = "false") String valor, Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        model.addAttribute("valor", valor);
        model.addAttribute("pesquisa", new Pesquisa());
        return "indexar";
    }

    @PostMapping("/indexar")
    public String resultadoIndexar(@ModelAttribute Pesquisa pesquisa, Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        System.out.println(pesquisa);
        try {
            if (h.indexarURL(cliente, pesquisa.getTextoPesquisa())) {
                model.addAttribute("valor", "true");
            } else {
                model.addAttribute("valor", "false");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "indexar";
    }

    @GetMapping("/indexarStories")
    public String indexarStories(@RequestParam(name = "contadorIndexado", defaultValue = "0") int contadorIndexado, @RequestParam(name = "numeroURLS", defaultValue = "0") int numeroURLS, Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        model.addAttribute("contadorIndexado", contadorIndexado);
        model.addAttribute("numeroURLS", numeroURLS);
        model.addAttribute("pesquisa", new Pesquisa());
        return "indexarStories";
    }

    @PostMapping("/indexarStories")
    public String resultadoIndexarStories(@ModelAttribute Pesquisa pesquisa, Model model) {
        int contadorIndexado = 0, numeroURLS;
        if (cliente == null) {
            return "redirect:/";
        }
        try {
            MyHackerNewsController hacker = new MyHackerNewsController();
            List<HackerNewsItemRecord> TopStories = hacker.UserTopStories(pesquisa.getTextoPesquisa());
            List<HackerNewsItemRecord> stories = new ArrayList<>();
            System.out.println(TopStories);
            numeroURLS = TopStories.size();
            for (HackerNewsItemRecord story : TopStories) {
                if (story.url() != null) {
                    if (h.indexarURL(cliente, story.url())) {
                        contadorIndexado++;
                        stories.add(story);
                    }
                }
            }
            model.addAttribute("contadorIndexado", contadorIndexado);
            model.addAttribute("numeroURLS", numeroURLS);
            model.addAttribute("TopStories", stories);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "indexarStories";
    }

    @GetMapping("/pesquisar")
    public String pesquisar(Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        model.addAttribute("pesquisa", new Pesquisa());
        return "pesquisar";
    }

    @PostMapping("/pesquisar")
    public String pesquisar(@ModelAttribute Pesquisa pesquisa, Model model, HttpServletRequest request) {
        if (cliente == null) {
            return "redirect:/";
        }
        HashSet<String[]> paginas;


        try {
            paginas = h.pesquisarPaginas(cliente, pesquisa.getTextoPesquisa());

            if (paginas == null) {
                model.addAttribute("mensagem", "Pesquisa não encontrada!");
            } else {

                ArrayList<String[]> listaDados = new ArrayList<>(paginas);
                model.addAttribute("paginas", listaDados);
            }
            String meuCheckbox = request.getParameter("meuCheckbox");
            if (meuCheckbox != null && meuCheckbox.equals("ativado")) {
                MyHackerNewsController hacker = new MyHackerNewsController();
                List<HackerNewsItemRecord> TopStories = hacker.hackerNewsTopStories(pesquisa.getTextoPesquisa());
                model.addAttribute("stories", TopStories);
                for (HackerNewsItemRecord story : TopStories) {
                    h.indexarURL(cliente, story.url());
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "pesquisar";
    }

    @GetMapping("/consultar")
    public String consultar(Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        model.addAttribute("pesquisa", new Pesquisa());
        return "consultar";
    }

    @PostMapping("/consultar")
    public String resultadoConsultar(@ModelAttribute Pesquisa pesquisa, Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        System.out.println(pesquisa);
        ArrayList<HashSet<String>> urls;
        try {
            urls = h.obterLinks(cliente, pesquisa.getTextoPesquisa());
            if (urls != null) {
                for (HashSet<String> s : urls) {
                    System.out.println(s);
                }
            }
            System.out.println(urls);
            if (urls == null || urls.isEmpty()) {
                model.addAttribute("mensagem", "URL não encontrado!");
            } else {
                ArrayList<String> listaDados = new ArrayList<>();

                for (HashSet<String> hashSet : urls) {
                    listaDados.addAll(hashSet);
                }
                model.addAttribute("urls", listaDados);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return "consultar";
    }

    @Scheduled(fixedDelay = 3000)
    public void atualizar() {
        try {
            if (cliente == null){
                return;
            }
            ArrayList<DownloaderInfo> downloaders = h.obterInfoDownloaders();
            ArrayList<Storage> barrels = h.obterInfoBarrels();
            HashMap<String, Integer> mapa = h.pesquisasFrequentes();
            System.out.println(downloaders);
            System.out.println(barrels);
            System.out.println(mapa);
            InfoGeral info = new InfoGeral(barrels, downloaders, mapa);
            messagingTemplate.convertAndSend("/topic/dados", info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/info")
    public String info() {
        if (cliente == null){
            return "redirect:/";
        }
        return "info";
    }

    @GetMapping("/logout")
    public String logout() {
        cliente = null;
        h = null;
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "valor1", defaultValue = "false") String valor1, @RequestParam(name = "valor2", defaultValue = "false") String valor2, Model model) {
        model.addAttribute("valor1", valor1);
        model.addAttribute("valor2", valor2);
        model.addAttribute("clienteInfo", new ClienteInfo());
        return "login";
    }


    @PostMapping("/login")
    public String resultadoLogin(@ModelAttribute ClienteInfo clienteInfo, Model model) {
        try {
            System.out.println(clienteInfo);
            h = (SearchModule_I) LocateRegistry.getRegistry(1100).lookup("Search_Module");
            if (clienteInfo.getNome() == null) {
                cliente = h.verificarLogin(clienteInfo.getUsername(), clienteInfo.getPassword());
                if (cliente == null) {
                    model.addAttribute("valor1", "false");
                    model.addAttribute("valor2", "true");

                }
            } else {
                cliente = h.verificarRegisto(clienteInfo.getNome(), clienteInfo.getEmail(), clienteInfo.getUsername(), clienteInfo.getPassword(), 1000);
                if (cliente == null) {
                    model.addAttribute("valor1", "true");
                    model.addAttribute("valor2", "false");
                }
            }
            System.out.println(cliente);
            if (cliente == null) {
                return "login";
            }

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        return "page";
    }
}