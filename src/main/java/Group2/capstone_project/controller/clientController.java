package Group2.capstone_project.controller;

import Group2.capstone_project.domain.Client;
import Group2.capstone_project.dto.client.ClientDto;
import Group2.capstone_project.service.clientService;
import Group2.capstone_project.session.SessionConst;
import Group2.capstone_project.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class clientController {


    private final PasswordEncoder passwordEncoder;
    private final clientService clientserivce;
    private final SessionManager sessionManager = new SessionManager();

    @Autowired
    public clientController(clientService clientService,PasswordEncoder passwordEncoder){
        this.clientserivce = clientService;
        this.passwordEncoder =passwordEncoder;
    }

    //@GetMapping("/")
    public String home(){
        return "/index.html";
    }

    @GetMapping("/")
    public String loginHome(HttpServletRequest request, Model model){
       HttpSession session = request.getSession(false);

        if(session == null){
            return "/index.html";
        }
        Client client = (Client) session.getAttribute(SessionConst.LOGIN_CLIENT);
        if(client==null)
            return "/index.html";

        System.out.println(client.getName());
        return "/capstone ver1/login_index.html";
    }



    @GetMapping("/client/home")
    public String main()
    {
        return "/client/clienthome";
    }

   // @PostMapping("/client/login")
    public String login(@ModelAttribute ClientDto clientDto, HttpSession Session, HttpServletResponse response){
        Client client = new Client();
        client.setId(clientDto.getId());
        client.setPwd(clientDto.getPassword());
           Optional<Client> result = clientserivce.login(client);
        if(result!=null) {
            Cookie idCookie = new Cookie("clientId", client.getId());
            response.addCookie(idCookie);
            Session.setAttribute("loginId",result.get().getId());
            return "redirect:/";
        }else{
            return "redirect:/capstone ver1/login.html";
            
        }
    }

    // @PostMapping("/clientlogin")
    public String loginV2(@ModelAttribute ClientDto clientDto, HttpServletRequest request, HttpServletResponse response){
        Client client = (Client)sessionManager.getSession(request);
        if(client !=null)
            return "redirect:/";
        Client client2 = new Client();
        client2.setId(clientDto.getId());
        client2.setPwd(clientDto.getPassword());
        Optional<Client> result = clientserivce.login(client2);
        if(result!=null) {
            sessionManager.createSession(client2, response);
            return "redirect:/";
        }else{
            return "redirect:/capstone ver1/login.html";

        }
    }

    @PostMapping("/clientlogin")
    public String loginV3(@ModelAttribute ClientDto clientDto, HttpServletRequest request){

        Client client = new Client();
        client.setId(clientDto.getId());
        client.setPwd(clientDto.getPassword());
        Optional<Client> result = clientserivce.login(client);
        if(result!=null) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_CLIENT, result.get() );
            return "redirect:/";
        }else{
            return "redirect:/capstone ver1/login.html";

        }
    }
    @GetMapping("/client/register")
    public String register(){
        return "/client/register";
    }

    @GetMapping("/client/findAccount")
    public String findAccount(){
        return  "/client/findAccount";
    }

    @PostMapping("/client/join")
    public String create(ClientDto ClientDto){

        Client client = new Client();
        client.setId(ClientDto.getId());
        client.setName(ClientDto.getName());
        client.setAge(ClientDto.getAge());
        client.setEmail(ClientDto.getEmail());
        client.setStudentNumber(ClientDto.getStudentNumber());
        client.setPwd(passwordEncoder.encode(ClientDto.getPassword()));
        client.setSchool(ClientDto.getSchool());
        clientserivce.join(client);

        return "redirect:/capstone ver1/login.html";
    }

    @GetMapping("/client")
    public String list(Model model){
        List<Client> clients = clientserivce.findAll();
        model.addAttribute("clients",clients);
        return "client/clientlist";
    }

    @GetMapping("client/findID")
    public String findID(Model model, @ModelAttribute ClientDto ClientDto){
        Client client = new Client();
        client.setName(ClientDto.getName());
        client.setStudentNumber(ClientDto.getStudentNumber());
        client.setAge(ClientDto.getAge());
        String result = clientserivce.findId(client.getName(), ClientDto.getStudentNumber(), client.getAge());
        model.addAttribute("result",result);
        return "client/checkyourId";
    }

    @GetMapping("client/findPwd")
    public String findPwd(Model model, ClientDto ClientDto){
        Client client = new Client();
        client.setName(ClientDto.getName());
        client.setId(ClientDto.getId());
        client.setStudentNumber(ClientDto.getStudentNumber());
        String result = clientserivce.findPwd(client.getName(), client.getId(), ClientDto.getStudentNumber());
        model.addAttribute("result",result);
        return "client/checkyourPwd";
    }
    @GetMapping("/client/update")
        public String updateForm(HttpSession session,Model model){
        String id = (String)session.getAttribute("loginId");
        Client client = clientserivce.updateForm(id);
        model.addAttribute("updateClient",client);
        return "client/clientinfoupdate";
    }

    @PostMapping("/client/update")
    public String updateClinet(Model model,@ModelAttribute ClientDto clientDto){
        Client client = new Client();
        client.setId(clientDto.getId());
        client.setName(clientDto.getName());
        client.setAge(clientDto.getAge());
        client.setStudentNumber(clientDto.getStudentNumber());
        clientserivce.updateInfo(client);
        model.addAttribute("client",client);
        return "/client/updateresult";
    }

    //@GetMapping("/client/logout")

    public String logOut(HttpSession httpSession, HttpServletResponse response){
        Cookie cookie = new Cookie("clientId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        httpSession.invalidate();
        return "redirect:/";
    }

   // @PostMapping ("/clientlogout")
    public String logOut(HttpServletRequest request, HttpServletResponse response){
        sessionManager.expire(request);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        return "redirect:/capstone ver1/login.html";
    }

     @PostMapping ("/clientlogout")
    public String logOut(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session!=null) {
            session.invalidate();
        }
        return "redirect:/capstone ver1/login.html";
    }



    @GetMapping("/board/home")

    public String boardHome(){
        return "/board/home";
    }
}
