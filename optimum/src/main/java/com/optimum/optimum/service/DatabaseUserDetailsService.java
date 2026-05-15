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
        return new User(
                account.getEmail(),
                account.getPasswordHash(),
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
        );
    }
}
