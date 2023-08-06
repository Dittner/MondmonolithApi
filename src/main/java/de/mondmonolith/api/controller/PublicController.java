package de.mondmonolith.api.controller;

import de.mondmonolith.api.controller.dto.Response;
import de.mondmonolith.api.controller.dto.UserDto;
import de.mondmonolith.api.model.User;
import de.mondmonolith.api.repository.DirRepo;
import de.mondmonolith.api.repository.DocRepo;
import de.mondmonolith.api.repository.PageRepo;
import de.mondmonolith.api.repository.UserRepo;
import de.mondmonolith.api.security.SecurityConfig;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class PublicController {


    @GetMapping("public")
    public Response getAllUsers() {
        try {
            List<ApiCmd> list = new ArrayList<>();
            list.add(new ApiCmd("GET", "/api/v1/auth"));
            list.add(new ApiCmd("POST", "/api/v1/signup"));
            list.add(new ApiCmd("GET", "/api/v1/users"));
            list.add(new ApiCmd("DELETE", "/api/v1/users/{userId}"));
            list.add(new ApiCmd("GET", "/api/v1/dirs"));
            list.add(new ApiCmd("POST", "/api/v1/dirs/create"));
            list.add(new ApiCmd("PUT", "/api/v1/dirs/update"));
            list.add(new ApiCmd("DELETE", "/api/v1/dirs/{dirId}"));
            list.add(new ApiCmd("GET", "/api/v1/dirs/{dirId}/docs"));
            list.add(new ApiCmd("POST", "dirs/{dirId}/docs/create"));
            list.add(new ApiCmd("PUT", "dirs/{dirId}/docs/update"));
            list.add(new ApiCmd("DELETE", "dirs/{dirId}/docs/{docId}"));
            list.add(new ApiCmd("GET", "dirs/{dirId}/docs/{docId}/pages"));
            list.add(new ApiCmd("POST", "dirs/{dirId}/docs/{docId}/pages/create"));
            list.add(new ApiCmd("PUT", "dirs/{dirId}/docs/{docId}/pages/update"));
            list.add(new ApiCmd("DELETE", "dirs/{dirId}/docs/{docId}/pages/{pageId}"));

            return new Response(list, HttpStatus.OK);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

class ApiCmd {
    public String method;
    public String name;
    public ApiCmd(String method, String name) {
        this.method = method;
        this.name = name;
    }
}
