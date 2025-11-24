package com.rairai.rairai_api.repository;

import com.rairai.rairai_api.model.AppUser;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<AppUser> findByAtivoTrue(Pageable pageable);
}
