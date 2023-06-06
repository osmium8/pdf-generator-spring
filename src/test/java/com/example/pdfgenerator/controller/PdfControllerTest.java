package com.example.pdfgenerator.controller;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.pdfgenerator.model.request.Invoice;
import com.example.pdfgenerator.model.request.Item;
import com.example.pdfgenerator.service.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.source.ByteArrayOutputStream;

@SpringBootTest("PdfControllerTest.class")
public class PdfControllerTest {

    @Mock
    private PdfService pdfGeneratorService;

    @InjectMocks
    private PdfController pdfController;

    @Test
    void generatePdf_whenValidInput_thenReturnsPDF() throws Exception {
        Invoice invoice = this.getStubInvoice();

        when(this.pdfGeneratorService.getPdfDocumentByteArray(invoice)).thenReturn(
                new ByteArrayOutputStream().assignBytes(new ObjectMapper().writeValueAsBytes(invoice)).toByteArray());

        ResponseEntity<byte[]> responseEntity = pdfController.generatePdf(invoice);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getHeaders().getContentType(), MediaType.APPLICATION_PDF);
        Assertions.assertEquals(responseEntity.getHeaders().getContentDisposition().getType(), "attachment");
        Assertions.assertEquals(responseEntity.getHeaders().getContentDisposition().getFilename(), "invoice.pdf");
        byte[] responseBody = responseEntity.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.length > 0);
    }

    private Invoice getStubInvoice() {
        Invoice invoice = new Invoice();
        invoice.setSeller("XYZ Pvt. Ltd.");
        invoice.setSellerGstin("29AABBCCDD121ZD");
        invoice.setSellerAddress("New Delhi, India");
        invoice.setBuyer("Vedant Computers");
        invoice.setBuyerGstin("29AABBCCDD131ZD");
        invoice.setBuyerAddress("New Delhi, India");

        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setName("Product 1");
        item.setQuantity("12 Nos");
        item.setRate(123.00);
        item.setAmount(1476.00);
        items.add(item);
        invoice.setItems(items);

        return invoice;
    }

}
