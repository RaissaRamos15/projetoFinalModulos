package com.rairai.rairai_api.service;

import com.rairai.rairai_api.dto.RegisterUserDTO;
import com.rairai.rairai_api.exception.EmailAlreadyExistsException;
import com.rairai.rairai_api.exception.ResourceNotFoundException;
import com.rairai.rairai_api.model.AppUser;
import com.rairai.rairai_api.repository.AppUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário a partir de DTO usado para registro/atualização.
     * NÃO realiza autenticação ou emissão de token — essa responsabilidade fica em outro serviço.
     *
     * @param dto DTO com os dados do usuário a ser criado
     * @return o usuário persistido
     * @throws UserAlreadyExistsException se o email já estiver em uso
     */
    @Transactional
    public AppUser createUser(RegisterUserDTO dto) {
        log.info("register dto: {} ", dto);

        if (repository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Este email já está em uso");
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        AppUser user = new AppUser();
        BeanUtils.copyProperties(dto, user);

        log.info("register user: {} ", user);

        return repository.save(user);
    }

    @Transactional(readOnly = true)
    @Cacheable("appUsers")
    public Page<AppUser> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        return repository.findByAtivoTrue(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "appUsers", key = "#id")
    public AppUser findById(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado")
            );
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        repository.deleteById(id);
    }

    /**
     * Atualiza os dados do usuário atualmente autenticado.
     * Utiliza utilitários de autenticação para extrair o usuário a ser atualizado.
     *
     * @param dto DTO com os novos dados
     * @return o usuário atualizado
     */
    @Transactional
    public AppUser update(RegisterUserDTO dto, Long id) {
        AppUser userToUpdate = repository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado")
            );

        // Atualiza apenas os campos que vieram no DTO
        if (dto.getNome() != null) {
            userToUpdate.setNome(dto.getNome());
        }
        if (dto.getEmail() != null) {
            userToUpdate.setEmail(dto.getEmail());
        }
        if (dto.getDataNascimento() != null) {
            userToUpdate.setDataNascimento(dto.getDataNascimento());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Atualiza o campo ativo (sempre, pois é um boolean primitivo)
        userToUpdate.setAtivo(dto.isAtivo());

        return repository.save(userToUpdate);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        log.info("loadUserByUsername called with: {}", username);
        var opt = repository.findByEmail(username);
        log.info("repository.findByEmail result present: {}", opt.isPresent());
        return opt.orElseThrow(() ->
            new UsernameNotFoundException("User not exists.")
        );
    }
}
