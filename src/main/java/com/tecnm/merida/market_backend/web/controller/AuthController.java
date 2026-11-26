package com.tecnm.merida.market_backend.web.controller;

import com.tecnm.merida.market_backend.domain.dto.AuthenticationRequest;
import com.tecnm.merida.market_backend.domain.dto.AuthenticationResponse;
import com.tecnm.merida.market_backend.persistence.crud.ClienteCrudRepository;
import com.tecnm.merida.market_backend.persistence.entity.Cliente;
import com.tecnm.merida.market_backend.web.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authenticate users and generate JWT tokens")
public class AuthController {

    @Autowired
    private ClienteCrudRepository clienteCrudRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostConstruct
    public void initDefaultPasswords() {
        String defaultEncodedPassword = passwordEncoder.encode("1234");
        Iterable<Cliente> clientes = clienteCrudRepository.findAll();
        for (Cliente cliente : clientes) {
            if (cliente.getPassword() == null || cliente.getPassword().isBlank()) {
                cliente.setPassword(defaultEncodedPassword);
                clienteCrudRepository.save(cliente);
            } else if (!cliente.getPassword().startsWith("$2a$")
                    && !cliente.getPassword().startsWith("$2b$")
                    && !cliente.getPassword().startsWith("$2y$")) {
                cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
                clienteCrudRepository.save(cliente);
            }
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate client and generate JWT",
            description = "Validate plain password against BCrypt hash in database and return a JWT token")
    @ApiResponse(responseCode = "200", description = "Authentication successful, JWT returned")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        String identifier = request.resolveIdentifier();
        if (identifier == null || request.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Cliente> clienteOpt = clienteCrudRepository.findById(identifier);
        if (clienteOpt.isEmpty()) {
            clienteOpt = clienteCrudRepository.findByCorreoElectronico(identifier);
        }
        if (clienteOpt.isEmpty()) {
            clienteOpt = clienteCrudRepository.findByNombre(identifier);
        }

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            if (cliente.getPassword() != null && passwordEncoder.matches(request.getPassword(), cliente.getPassword())) {
                String token = jwtUtil.generateToken(identifier);
                return ResponseEntity.ok(new AuthenticationResponse(token));
            }
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
