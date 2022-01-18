package com.cognizant.supplyshop.controller;

import com.cognizant.supplyshop.model.Product;
import com.cognizant.supplyshop.repository.ProductRepository;
import com.cognizant.supplyshop.service.AuthService;
import com.cognizant.supplyshop.service.FilesStorageService;
import com.cognizant.supplyshop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    @Autowired private ProductRepository productRepository;
    @Autowired private AuthService authService;
//    @Autowired FilesStorageService storageService;
    @Autowired private StorageService storageService;

    @GetMapping
    public List<Product> getProducts() {
        return productRepository.findByDeleted(false);
    }

    @GetMapping("{id}")
    public Optional<Product> getProduct(@PathVariable Long id) {
        return productRepository.findByIdAndDeleted(id, false);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestParam("image") MultipartFile imageCover, Product product) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        //if (productRepository.existsByName(product.getName())) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        String fileName = System.currentTimeMillis() + imageCover.getOriginalFilename();
        try {
//            storageService.save(imageCover, fileName);
            storageService.uploadFile(imageCover, fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not store image");
        }
        product.setImageCover(fileName);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    @RequestMapping(value = "/images/{filename:.+}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
//        Resource image = storageService.load(filename);

        try {
//            byte[] bytes = StreamUtils.copyToByteArray(image.getInputStream());
            byte[] bytes = storageService.downloadFile(filename);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not store image");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProductWithImage(@RequestParam(name = "image", required = false) MultipartFile imageCover,
                                                    @PathVariable Long id,
                                                    Product product) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        Optional<Product> getProduct = productRepository.findById(id);
        if (getProduct.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Product oldProduct = getProduct.get();
        if (imageCover != null) {
            String fileName = System.currentTimeMillis() + imageCover.getOriginalFilename();
            if (!storageService.uploadFile(imageCover, fileName))
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Save new file
            storageService.deleteFile(oldProduct.getImageCover()); // Delete old file
            product.setImageCover(fileName); // Update product filename
        } else product.setImageCover(oldProduct.getImageCover());
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (!authService.isAdmin()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Authenticate admin
        if (!productRepository.existsById(id)) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Product product = productRepository.getById(id);
        storageService.deleteFile(product.getImageCover());
        product.setDeleted(true);
        productRepository.save(product); // Don't want to actually delete product or causes problems with order
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
