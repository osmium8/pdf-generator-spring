package com.example.pdfgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdfgenerator.model.request.Invoice;
import com.example.pdfgenerator.service.PdfService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class PdfController {
    @Autowired
    private PdfService pdfGeneratorService;

    @PostMapping("/api/v1/pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody Invoice invoice) {
        try {
            byte[] byteArray = this.pdfGeneratorService.getPdfDocumentByteArray(invoice);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("invoice.pdf").build());
            return new ResponseEntity<>(byteArray, headers, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
