package com.optimum.optimum.service;

import com.optimum.optimum.model.UserAccount;
import com.optimum.optimum.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserAccountRepository accounts;

    public DatabaseUserDetailsService(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserAccount account = accounts.findByEmail(login.toLowerCase())
                .or(() -> accounts.findByUsername(login))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        return User.builder()
                .username(account.getEmail())
                .password(account.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
                .accountLocked(!account.isActive())  // bloque les comptes suspendus
                .build();
    }
}
