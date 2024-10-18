package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.ProdReadResponseDTO;
import org.example.shoppingweather.dto.ProdUploadRequestDTO;
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

    public Long save(ProdUploadRequestDTO dto) throws IOException {
        // Save main picture and get its path
        String mainPicturePath = saveFile(dto.getMainPicture());

        Product product = dto.toProduct();
        product.setMainPicture(mainPicturePath);    // Store the main picture path as a String

        // Create and save the Product entity
        return productRepository.save(product).getId();
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
}
