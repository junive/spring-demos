package com.junive.account.service;

import com.junive.account.exception.CustomExceptionConflict;
import com.junive.account.exception.CustomExceptionNotFound;
import com.junive.account.util.CustomStatus;
import com.junive.account.model.AppUser;
import com.junive.account.model.Role;
import com.junive.account.repository.RoleRepository;
import com.junive.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = this.getUserByUsername(username);
        log.info("User found in the database: {}", username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new User( user.getUsername(), user.getPassword(), authorities);
    }

    public AppUser saveUser(AppUser user) {
        log.info("Saving new user {} to the database", user.getName());

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw CustomExceptionConflict.builder()
                    .customStatus(CustomStatus.USERNAME_EXIST)
                    .badValue(user.getUsername()).build();
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw CustomExceptionConflict.builder()
                    .customStatus(CustomStatus.EMAIL_EXIST)
                    .badValue(user.getEmail()).build();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AppUser savedUser = userRepository.save(user);
        this.addRoleToUser(user.getUsername(), "ROLE_USER");
        return savedUser;
    }

    public Role saveRole(Role role) {
        log.info("Saving new role  {} to the database", role.getName());
        return roleRepository.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        AppUser user = this.getUserByUsername(username);
        Role role = this.getRoleByName(roleName);
        log.info("Adding role {} to the user {}", roleName, username);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public Role getRoleByName(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw CustomExceptionNotFound.builder()
                    .customStatus(CustomStatus.ROLE_NOT_FOUND)
                    .badValue(roleName).build();
        }
        log.info("Fetching role {}", roleName);
        return role;
    }

    public AppUser getUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User Not Found");
        log.info("Fetching user {}", username);
        return user;
    }

    public List<AppUser> getUsers() {
        log.info("Fetching user all users");
        return userRepository.findAll();
    }


}
