package org.example.billmanagement.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.billmanagement.controller.dto.LoginDto;
import org.example.billmanagement.controller.dto.TransactionDto;
import org.example.billmanagement.service.BillCalculationService;
import org.example.billmanagement.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BillCalculationController {

    private final BillCalculationService billCalculationService;

    @GetMapping("/calculate/{groupId}")
    public ResponseEntity<List<TransactionDto>> calculate(
            @PathVariable(value = "groupId", required = false) final Long groupId) {
        return ResponseEntity.ok(billCalculationService.calculate(groupId));
    }
}
