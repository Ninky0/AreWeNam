package org.example.shoppingweather.controller;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ProdUploadRequestDTO;
import org.example.shoppingweather.dto.ProdUploadResponseDTO;
import org.example.shoppingweather.dto.sign.SignUpResponseDTO;
import org.example.shoppingweather.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    @PostMapping("/product/upload")
    public ResponseEntity<ProdUploadResponseDTO> uploadProduct(@RequestBody ProdUploadRequestDTO prodUploadRequestDTO){
        adminService.save(prodUploadRequestDTO);
        return ResponseEntity.ok(
                ProdUploadResponseDTO.builder()
                        .url("/admin/product")
                        .build()
        );
    }
}
