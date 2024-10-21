package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.product.ProdUploadRequestDTO;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;

    public List<ProdReadResponseDTO> findAll(){
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(Product::toProdReadResponseDTO)
                .collect(Collectors.toList());
    }
    public ProdReadResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        return product.toProdReadResponseDTO();
    }
    public Long save(ProdUploadRequestDTO dto) throws IOException {
        // Save main picture and get its path
        String mainPicturePath = saveFile(dto.getMainPicture());

        Product product = dto.toProduct();
        product.setMainPicture(mainPicturePath);    // Store the main picture path as a String

        // Create and save the Product entity
        return productRepository.save(product).getId();
    }
    public void updateProduct(Long id, ProdUploadRequestDTO dto) throws IOException {
        // 기존 상품을 찾습니다
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));

        // 기존 상품 정보를 수정합니다
        existingProduct.setName(dto.getName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setQuantity(dto.getQuantity());
        existingProduct.setCategory(dto.getCategory());
        existingProduct.setSeason(dto.getSeason());
        existingProduct.setTemperature(dto.getTemperature());
        existingProduct.setDescription(dto.getDescription());

        // 메인 이미지가 변경된 경우 처리
        if (dto.getMainPicture() != null && !dto.getMainPicture().isEmpty()) {
            String mainPicturePath = saveFile(dto.getMainPicture());
            existingProduct.setMainPicture(mainPicturePath);
        }

        // 수정된 상품을 저장합니다
        productRepository.save(existingProduct);
    }
    public String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        // Ensure the upload directory exists
        if (!Files.exists(Paths.get(uploadDir))) {
            Files.createDirectories(Paths.get(uploadDir));
        }

        // Check for file existence and generate a unique file name if needed
        filePath = generateUniqueFilePath(filePath);

        // Save the file
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString(); // Return the saved file path
    }

    private Path generateUniqueFilePath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        Path uniquePath = filePath;
        int counter = 1;

        while (Files.exists(uniquePath)) {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
            String newFileName = fileNameWithoutExt + "_" + counter + fileExtension;
            uniquePath = filePath.getParent().resolve(newFileName);
            counter++;
        }

        return uniquePath;
    }

    public void deleteProductsByIds(List<Long> productIds) {
        productRepository.deleteAllById(productIds);
    }
}
