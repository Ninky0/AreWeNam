package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.product.ProdUploadRequestDTO;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());
    private final ProductRepository productRepository;

    public List<ProdReadResponseDTO> findAll(){
        return productRepository.findAll().stream()
                .map(Product::toProdReadResponseDTO)
                .collect(Collectors.toList());
    }

    public ProdReadResponseDTO findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id))
                .toProdReadResponseDTO();
    }

    public Long save(ProdUploadRequestDTO dto) throws IOException {
        String mainPicturePath = saveFile(dto.getMainPicture());
        Product product = dto.toProduct();
        product.setMainPicture(mainPicturePath);
        Product savedProduct = productRepository.save(product);
        LOGGER.info("Product saved with ID: " + savedProduct.getId());
        return savedProduct.getId();
    }

    public Long findMaxId(){
        return productRepository.findMaxId();
    }

    public void updateProduct(Long id, ProdUploadRequestDTO dto) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        updateProductDetails(existingProduct, dto);
        productRepository.save(existingProduct);
        LOGGER.info("Product updated with ID: " + existingProduct.getId());
    }

    private void updateProductDetails(Product product, ProdUploadRequestDTO dto) throws IOException {
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
        product.setSeason(dto.getSeason());
        product.setTemperature(dto.getTemperature());
        product.setDescription(dto.getDescription());
        if (dto.getMainPicture() != null && !dto.getMainPicture().isEmpty()) {
            String mainPicturePath = saveFile(dto.getMainPicture());
            product.setMainPicture(mainPicturePath);
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/uploads/";
        String fileName = file.getOriginalFilename();
        return getString(file, fileName, uploadDir);
    }

    public String saveDetail(MultipartFile file, String name) throws IOException {
        String uploadDir = "src/main/resources/static/uploads/";
        return getString(file, name, uploadDir);
    }

    private String getString(MultipartFile file, String name, String uploadDir) throws IOException {
        Path filePath = Paths.get(uploadDir + name);

        if (!Files.exists(Paths.get(uploadDir))) {
            Files.createDirectories(Paths.get(uploadDir));
        }
        filePath = generateUniqueFilePath(filePath);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        return filePath.toString(); // Return the saved file path
    }

    private Path generateUniqueFilePath(Path filePath) {
        Path uniquePath = filePath;
        int counter = 1;
        while (Files.exists(uniquePath)) {
            String baseName = StringUtils.stripFilenameExtension(filePath.getFileName().toString());
            String extension = ".jpg";
            String newFileName = baseName + "_" + counter + extension;
            uniquePath = filePath.getParent().resolve(newFileName);
            counter++;
        }
        return uniquePath;
    }

    public void deleteProductsByIds(List<Long> productIds) {
        productRepository.deleteAllById(productIds);
        LOGGER.info("Deleted products with IDs: " + productIds);
    }
}
