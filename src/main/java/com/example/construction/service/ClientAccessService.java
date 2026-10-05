package com.example.construction.service;

import com.example.construction.entity.Client;
import com.example.construction.entity.Project;
import com.example.construction.entity.User;
import com.example.construction.repository.ClientRepository;
import com.example.construction.repository.ProjectRepository;
import com.example.construction.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Resolves the client profile represented by the current authenticated user.
 * Client ownership is intentionally based on the existing User.email ->
 * Client.email relationship, so no database change is required.
 */
@Service
public class ClientAccessService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;

    public ClientAccessService(UserRepository userRepository,
                               ClientRepository clientRepository,
                               ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
    }

    public Optional<Client> getCurrentClient(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return userRepository.findByUsername(authentication.getName())
                .map(User::getEmail)
                .filter(email -> !email.isBlank())
                .flatMap(clientRepository::findByEmail);
    }

    public Optional<Project> getOwnedProject(Authentication authentication,
                                             Integer projectId) {
        return getCurrentClient(authentication)
                .flatMap(client -> projectRepository.findById(projectId)
                        .filter(project -> client.getClientId()
                                .equals(project.getClientId())));
    }
}
