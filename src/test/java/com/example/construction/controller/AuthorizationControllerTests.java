package com.example.construction.controller;

import com.example.construction.config.securityConfig;
import com.example.construction.entity.User;
import com.example.construction.repository.UserRepository;
import com.example.construction.service.NotificationService;
import com.example.construction.service.ClientAccessService;
import com.example.construction.service.ProjectService;
import com.example.construction.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, NotificationController.class,
        ProjectController.class})
@Import(securityConfig.class)
class AuthorizationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ClientAccessService clientAccessService;

    @Test
    @WithMockUser(username = "client.one", roles = "CLIENT")
    void clientCannotAccessWorkerManagementApi() throws Exception {
        mockMvc.perform(get("/api/workers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "client.one", roles = "CLIENT")
    void usersEndpointReturnsOnlyTheAuthenticatedUser() throws Exception {
        User currentUser = user(7, "client.one");
        when(userRepository.findByUsername("client.one"))
                .thenReturn(Optional.of(currentUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(7));
    }

    @Test
    @WithMockUser(username = "client.one", roles = "CLIENT")
    void notificationEndpointHidesAnotherUsersNotifications() throws Exception {
        when(userRepository.findByUsername("client.one"))
                .thenReturn(Optional.of(user(7, "client.one")));

        mockMvc.perform(get("/api/notifications/user/8"))
                .andExpect(status().isNotFound());

        verify(notificationService, never()).getNotifications(anyInt());
    }

    @Test
    @WithMockUser(username = "client.one", roles = "CLIENT")
    void clientCannotReadAnotherClientsProjectById() throws Exception {
        when(clientAccessService.getOwnedProject(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(99)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound());

        verify(projectService, never()).getProjectById(99);
    }

    private User user(Integer id, String username) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setEmail("client@example.test");
        user.setFullName("Client One");
        user.setRole("CLIENT");
        return user;
    }
}
