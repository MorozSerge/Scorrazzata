package com.example.proj.controller;

import com.example.proj.domain.Message;
import com.example.proj.domain.User;
import com.example.proj.repos.MessageRepo;
import com.example.proj.repos.UserRepo;
import com.example.proj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;

@Controller
public class MainController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting( Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(
            @AuthenticationPrincipal User user,
            Model model,
            @RequestParam(name = "show", defaultValue = "false") boolean show,
            @RequestParam(name = "range", required = false) String range){
        model.addAttribute("user", user);
        if (show) {
            ArrayList<User> userList = new ArrayList<User>();
            Double lat = user.getLat();
            Double lon = user.getLon();
            for (User user1 : userRepo.findAll()) {
                if ((Double.parseDouble(range) >
                        (2 * 6371008) * asin(Math.sqrt(sin((lat - user1.getLat()) * acos(0) / 180) * sin((lat - user1.getLat()) * acos(0) / 180)
                                + cos(lat * acos(0) / 180) * cos(user1.getLat() * acos(0) / 180) * sin((lon - user1.getLon()) * acos(0) / 180) * sin((lon - user1.getLon()) * acos(0) / 180))))
                        && !user.getId().equals(user1.getId())){
                    userList.add(user1);
                }
            }
             model.addAttribute("users", userList);
        }
        else
            model.addAttribute("users", null);
        return "main";
    }

    @PostMapping("/main")
    public String set (
            @AuthenticationPrincipal User user,
            @RequestParam(name ="lon", required = false) String lon,
            @RequestParam(name ="lat", required = false) String lat,
            @RequestParam( defaultValue = "false") boolean set,
            @RequestParam(name ="some", defaultValue = "500") String range,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
            )  {
        if (set) {
            user.setLat(Double.parseDouble(lat));
            user.setLon(Double.parseDouble(lon));
            userRepo.save(user);
            messageRepo.findByAuthor(user, pageable);
            return "redirect:/main";
        }
        String s = range;
        return "redirect:/main?show=true&range=" + s;
    }

    private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }
    }


    @GetMapping("/user-messages/{user}")
    public String userMessges(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            Model model,
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Message> page;
        Set<Message> messages = user.getMessages();
        page = messageRepo.findByAuthor(user,pageable);

        model.addAttribute("userChannel", user);
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(user));
        model.addAttribute("page", page);
        model.addAttribute("url", "/user-messages/" + user.getId());
        return "userMessages";
    }


    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }

            if (!StringUtils.isEmpty(tag)) {
                message.setTag(tag);
            }

            saveFile(message, file);

            messageRepo.save(message);
        }

        return "redirect:/user-messages/" + user;
    }

    @GetMapping("/message")
    public String main(
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Message> page;

        if (filter != null && !filter.isEmpty()) {
            page = messageRepo.findByTag(filter, pageable);
        } else {
            page = messageRepo.findAll(pageable);
        }

        model.addAttribute("page", page);
        model.addAttribute("url", "/message");
        model.addAttribute("filter", filter);

        return "message";
    }

    @PostMapping("/message")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        message.setAuthor(user);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            saveFile(message, file);

            model.addAttribute("message", null);

            messageRepo.save(message);
        }

        Iterable<Message> messages = messageRepo.findAll();

        model.addAttribute("messages", messages);

        return "redirect:/message";
    }

}