package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.product.ProdUploadRequestDTO;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    public Long findMaxId(){
        return productRepository.findMaxId();
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
        String uploadDir = "src/main/resources/static/uploads/"; //저장위치는 이게 맞는듯
        BufferedImage image = ImageIO.read(file.getInputStream());
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";
        Path filePath = Paths.get(uploadDir, fileName);
        if (!Files.exists(Paths.get(uploadDir))) {
            Files.createDirectories(Paths.get(uploadDir));
        }
        filePath = generateUniqueFilePath(filePath);
        ImageIO.write(image, "jpg", filePath.toFile());

        return filePath.toString();
    }

    private Path generateUniqueFilePath(Path filePath) {
        Path uniquePath = filePath;
        String baseName = StringUtils.stripFilenameExtension(filePath.getFileName().toString());
        String extension = ".jpg";
        int counter = 1;

        while (Files.exists(uniquePath)) {
            String newFileName = baseName + "_" + counter + extension;
            uniquePath = filePath.getParent().resolve(newFileName);
            counter++;
        }
        return uniquePath;
    }

    public void deleteProductsByIds(List<Long> productIds) {
        productRepository.deleteAllById(productIds);
    }
}
