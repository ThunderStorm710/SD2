package com.example.servingwebcontent;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.example.servingwebcontent.beans.Number;
import com.example.servingwebcontent.forms.Pesquisa;
import com.example.servingwebcontent.forms.Project;
import com.example.servingwebcontent.src.src.ClienteInfo;
import com.example.servingwebcontent.src.src.DownloaderInfo;
import com.example.servingwebcontent.src.src.SearchModule_I;
import com.example.servingwebcontent.src.src.Storage;
import com.example.servingwebcontent.thedata.Employee;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Controller
public class GreetingController {

    private ClienteInfo cliente;
    private SearchModule_I h;

    @Resource(name = "requestScopedNumberGenerator")
    private Number nRequest;

    @Resource(name = "sessionScopedNumberGenerator")
    private Number nSession;

    @Resource(name = "applicationScopedNumberGenerator")
    private Number nApplication;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Number requestScopedNumberGenerator() {
        return new Number();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Number sessionScopedNumberGenerator() {
        return new Number();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Number applicationScopedNumberGenerator() {
        return new Number();
    }

    /*@GetMapping("/")
    public String redirect() {
        return "redirect:/home";
    }*/

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

    @GetMapping("/pesquisar")
    public String pesquisar(Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        model.addAttribute("pesquisa", new Pesquisa());
        return "pesquisar";
    }

    @PostMapping("/pesquisar")
    public String pesquisar(@ModelAttribute Pesquisa pesquisa, Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        System.out.println(pesquisa);
        System.out.println(cliente);
        HashSet<String[]> paginas;
        try {
            paginas = h.pesquisarPaginas(cliente, pesquisa.getTextoPesquisa());
            if (paginas != null) {
                for (String[] p : paginas) {

                    System.out.println(p[0] + "-" + p[1] + " tam " + p.length);
                }
            }
            System.out.println(paginas);


            if (paginas == null) {
                model.addAttribute("mensagem", "Pesquisa não encontrada!");
            } else {
                model.addAttribute("paginas", paginas);
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
                model.addAttribute("urls", urls);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return "consultar";
    }

    @GetMapping("/info")
    public String info(Model model)  {
        if (cliente == null) {
            return "redirect:/";
        }
        try {
            ArrayList<DownloaderInfo> downloaders = h.obterInfoDownloaders();
            ArrayList<Storage> barrels = h.obterInfoBarrels();
            HashMap<String, Integer> mapa = h.pesquisasFrequentes();
            System.out.println(downloaders);
            System.out.println(barrels);
            System.out.println(mapa);
            model.addAttribute("d", downloaders == null || downloaders.isEmpty());
            model.addAttribute("b", barrels == null || barrels.isEmpty());
            model.addAttribute("m", mapa == null || mapa.isEmpty());
            model.addAttribute("downloaders", downloaders);
            model.addAttribute("barrels", barrels);
            model.addAttribute("mapa", mapa);

        } catch (RemoteException e){
            e.printStackTrace();
        }
        return "info";
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("othername", "SD");
        return "greeting";
    }

    @GetMapping("/home")
    public String pesquisa(Model model) {
        if (cliente == null) {
            return "redirect:/";
        }
        model.addAttribute("pesquisa", new Pesquisa());
        return "home";
    }


    @GetMapping("/login")
    public String login(@RequestParam(name = "valor", defaultValue = "false") String valor, Model model) {
        model.addAttribute("valor", valor);
        model.addAttribute("clienteInfo", new ClienteInfo());
        return "login";
    }


    @PostMapping("/login")
    public String resultadoLogin(@ModelAttribute ClienteInfo clienteInfo, Model model) {
        try {
            System.out.println(clienteInfo);
            h = (SearchModule_I) LocateRegistry.getRegistry(1100).lookup("Search_Module");
            if (clienteInfo.getNome() == null) {
                System.out.println("FIZ LOGIN");
                cliente = h.verificarLogin(clienteInfo.getUsername(), clienteInfo.getPassword());
            } else {
                System.out.println("FIZ REGISTO");
                cliente = h.verificarRegisto(clienteInfo.getNome(), clienteInfo.getEmail(), clienteInfo.getUsername(), clienteInfo.getPassword(), 1000);
            }
            System.out.println(cliente);
            if (cliente == null) {
                model.addAttribute("valor", "false");
                return "login";
            }

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        return "page";
    }

    @GetMapping("/givemeatable")
    public String atable(Model model) {
        Employee[] theEmployees = {new Employee(1, "José", "9199999", 1890), new Employee(2, "Marisa", "9488444", 2120), new Employee(3, "Hélio", "93434444", 2500)};
        List<Employee> le = new ArrayList<>();
        Collections.addAll(le, theEmployees);
        model.addAttribute("emp", le);
        return "table";
    }

    @GetMapping("/create-project")
    public String createProjectForm(Model model) {

        model.addAttribute("project", new Project());
        return "create-project";
    }

    @PostMapping("/save-project")
    public String saveProjectSubmission(@ModelAttribute Project project) {

        // TODO: save project in DB here

        return "result";
    }

    @GetMapping("/counters")
    public String counters(Model model) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        Integer counter = (Integer) session.getAttribute("counter");
        int c;
        if (counter == null)
            c = 1;
        else
            c = counter + 1;
        session.setAttribute("counter", c);
        model.addAttribute("sessioncounter", c);
        model.addAttribute("requestcounter2", this.nRequest.next());
        model.addAttribute("sessioncounter2", this.nSession.next());
        model.addAttribute("applicationcounter2", this.nApplication.next());
        return "counter";
    }

}