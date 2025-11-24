package com.rairai.rairai_api.service;

import com.rairai.rairai_api.config.JWTUtil;
import com.rairai.rairai_api.dto.AuthResponseDTO;
import com.rairai.rairai_api.dto.LoginUserDTO;
import com.rairai.rairai_api.dto.RegisterUserDTO;
import com.rairai.rairai_api.model.AppUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final AppUserService appUserService;

    @Autowired
    public AuthenticationService(
        AuthenticationManager authenticationManager,
        JWTUtil jwtUtil,
        AppUserService appUserService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.appUserService = appUserService;
    }

    /**
     * Autentica um usuário com as credenciais fornecidas e, em caso de sucesso,
     * devolve um DTO contendo o token JWT.
     *
     * @param dto DTO com email e password (raw)
     * @return AuthResponseDTO contendo mensagem e token JWT
     */
    public AuthResponseDTO login(LoginUserDTO dto) {
        try {
            log.info("dto in login: {}", dto);

            var authToken = new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
            );

            log.info("auth token: {}", authToken);

            // Lança AuthenticationException se credenciais inválidas
            authenticationManager.authenticate(authToken);

            AuthResponseDTO response = new AuthResponseDTO();
            response.setMessage("User logged in successfully");
            response.setToken(jwtUtil.buildToken(dto.getEmail()));
            return response;
        } catch (AuthenticationException ex) {
            // Reutiliza a exceção de domínio já existente para manter consistência com o projeto
            log.info("error: {}", ex.getMessage());
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }
    }

    /**
     * Registra um novo usuário e retorna o resultado do login imediato (token JWT).
     * Delega a criação do usuário ao AppUserService e em seguida realiza autenticação.
     *
     * Nota: o {@link AppUserService#createUser} é responsável por codificar a senha ao persistir.
     *
     * @param dto DTO com dados do usuário (incluindo password em texto plano)
     * @return AuthResponseDTO contendo mensagem e token JWT
     */
    @Transactional
    public AuthResponseDTO register(RegisterUserDTO dto) {
        log.info("dto before app user service: {}", dto);
        RegisterUserDTO sendedData = new RegisterUserDTO();
        BeanUtils.copyProperties(dto, sendedData);

        AppUser user = appUserService.createUser(dto);
        log.info("dto: {}", sendedData);
        log.info("user: {}", user);
        // Após criação, autentica usando as credenciais originais (raw password)
        LoginUserDTO loginDto = new LoginUserDTO(
            sendedData.getEmail(),
            sendedData.getPassword()
        );
        AuthResponseDTO result = login(loginDto);
        return result;
    }

    @Transactional
    public AuthResponseDTO updateAndRegister(RegisterUserDTO dto, Long id) {
        log.info("dto before app user service: {}", dto);
        RegisterUserDTO sendedData = new RegisterUserDTO();
        BeanUtils.copyProperties(dto, sendedData);

        AppUser user = appUserService.update(dto, id);

        log.info("dto: {}", sendedData);
        log.info("user: {}", user);

        LoginUserDTO loginDto = new LoginUserDTO(
            sendedData.getEmail(),
            sendedData.getPassword()
        );
        AuthResponseDTO result = login(loginDto);
        return result;
    }
}
