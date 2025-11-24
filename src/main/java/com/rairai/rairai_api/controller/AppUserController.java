package com.rairai.rairai_api.controller;

import com.rairai.rairai_api.dto.AuthResponseDTO;
import com.rairai.rairai_api.dto.LoginUserDTO;
import com.rairai.rairai_api.dto.RegisterUserDTO;
import com.rairai.rairai_api.exception.BadRequestException;
import com.rairai.rairai_api.model.AppUser;
import com.rairai.rairai_api.service.AppUserService;
import com.rairai.rairai_api.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<Page<AppUser>> findAll(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<AppUser> users = appUserService.findAll(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> findById(@PathVariable Long id) {
        AppUser appUser = appUserService.findById(id);
        return ResponseEntity.ok(appUser);
    }

    @GetMapping("/me")
    public ResponseEntity<AppUser> findByToken() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        AppUser user = (AppUser) principal;
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
        @Valid @RequestBody RegisterUserDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Dados de registro inválidos",
                bindingResult
            );
        }

        AuthResponseDTO response = authenticationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
        @Valid @RequestBody LoginUserDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Dados de login inválidos",
                bindingResult
            );
        }

        AuthResponseDTO response = authenticationService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResponseDTO> updateAndLogin(
        @Valid @RequestBody RegisterUserDTO dto,
        BindingResult bindingResult,
        @PathVariable Long id
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Dados de atualização inválidos",
                bindingResult
            );
        }

        AuthResponseDTO response = authenticationService.updateAndRegister(
            dto,
            id
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appUserService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
