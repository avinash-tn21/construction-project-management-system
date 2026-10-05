package com.example.construction.controller;

import com.example.construction.entity.Client;
import com.example.construction.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // READ - Get all clients
    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    // READ - Get one client
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Integer id) {

        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientService.saveClient(client);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(
            @PathVariable Integer id,
            @RequestBody Client clientDetails) {

        return clientService.getClientById(id)
                .map(client -> {

                    client.setClientName(clientDetails.getClientName());
                    client.setPhone(clientDetails.getPhone());
                    client.setEmail(clientDetails.getEmail());
                    client.setAddress(clientDetails.getAddress());

                    Client updatedClient =
                            clientService.saveClient(client);

                    return ResponseEntity.ok(updatedClient);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Integer id) {

        if (clientService.getClientById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        clientService.deleteClient(id);

        return ResponseEntity.noContent().build();
    }
}