package org.example.shoppingweather.service;

import lombok.RequiredArgsConstructor;
import org.example.shoppingweather.dto.product.ProdReadResponseDTO;
import org.example.shoppingweather.dto.product.ProdUploadRequestDTO;
import org.example.shoppingweather.entity.Product;
import org.example.shoppingweather.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<ProdReadResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(Product::toProdReadResponseDTO);
    }

    public ProdReadResponseDTO findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id))
                .toProdReadResponseDTO();
    }

    public Long save(ProdUploadRequestDTO dto) throws IOException {
        String mainPicturePath = saveFile(dto.getMainPicture());
        Product product = dto.toProduct();
        product.setMainPicturePath(mainPicturePath); // 경로를 mainPicturePath에 저장
        Product savedProduct = productRepository.save(product);
        LOGGER.info("Product saved with ID: " + savedProduct.getId());
        return savedProduct.getId();
    }

    public void updateProduct(Long id, ProdUploadRequestDTO dto) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        updateProductDetails(existingProduct, dto);
        productRepository.save(existingProduct);
        LOGGER.info("Product updated with ID: " + existingProduct.getId());
    }

    private void updateProductDetails(Product product, ProdUploadRequestDTO dto) throws IOException {
        // 기본 필드 업데이트
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
        product.setSeason(dto.getSeason());
        product.setTemperature(dto.getTemperature());
        product.setDescription(dto.getDescription());

        // 새 메인 이미지가 있는 경우 처리
        if (dto.getMainPicture() != null && !dto.getMainPicture().isEmpty()) {
            // 기존 이미지가 있다면 삭제
            if (product.getMainPicturePath() != null) {
                Path previousImagePath = Paths.get("src/main/resources/static" + product.getMainPicturePath());
                Files.deleteIfExists(previousImagePath);
            }

            // 새로운 이미지 저장 후 경로 설정
            String mainPicturePath = saveFile(dto.getMainPicture());
            product.setMainPicturePath(mainPicturePath); // 새 이미지 경로를 mainPicturePath에 저장
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/uploads/";
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = Paths.get(uploadDir + fileName);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 클라이언트에서 접근할 수 있는 상대 경로를 반환
        return "/uploads/" + fileName;
    }

    public String saveDetail(MultipartFile file, String name) throws IOException {
        String uploadDir = "src/main/resources/static/uploads/";
        String fileName = generateUniqueFileName(name);
        Path filePath = Paths.get(uploadDir + fileName);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + fileName;
    }

    private String generateUniqueFileName(String originalName) {
        String baseName = StringUtils.stripFilenameExtension(originalName);
        String extension = StringUtils.getFilenameExtension(originalName);
        String fileName = baseName + "_" + System.currentTimeMillis() + "." + extension;
        return fileName;
    }

    public void deleteProductsByIds(List<Long> productIds) {
        productRepository.deleteAllById(productIds);
        LOGGER.info("Deleted products with IDs: " + productIds);
    }

    public Page<Product> getProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAll(pageable);
    }
}
