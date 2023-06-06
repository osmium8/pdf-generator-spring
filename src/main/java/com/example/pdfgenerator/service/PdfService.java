package com.example.pdfgenerator.service;

import com.itextpdf.io.source.ByteArrayOutputStream;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pdfgenerator.entity.CacheData;
import com.example.pdfgenerator.model.request.Invoice;
import com.example.pdfgenerator.model.request.Item;
import com.example.pdfgenerator.repository.CacheDataRepo;
import com.example.pdfgenerator.util.ConverterUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

@Service
public class PdfService {

    @Autowired
    private CacheDataRepo cacheDataRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Returns {@code byte[]} Array of the {@link PdfDocument} generated from given {@code Invoice} Object
     * @param invoice Invoice Object containing data
     * @throws JsonProcessingException for invalid Invoice object
     */
    public byte[] getPdfDocumentByteArray(Invoice invoice) throws JsonProcessingException {
        byte[] byteArray;

        String invoiceAsJsonString = objectMapper.writeValueAsString(invoice);

        Optional<CacheData> optionalCacheData = cacheDataRepository.findById(invoiceAsJsonString);

        if (optionalCacheData.isPresent()) {
            String encodedString = optionalCacheData.get().getValue();
            byteArray = java.util.Base64.getDecoder().decode(encodedString.getBytes());
        } else {
            ByteArrayOutputStream outputStream = this.generateByteArray(invoice);
            byteArray = outputStream.toByteArray();
            byte[] encoded = java.util.Base64.getEncoder().encode(byteArray);
            // cache new invoice
            String string = ConverterUtil.convertByteArrayToString(encoded);
            CacheData cacheData = new CacheData(invoiceAsJsonString, string);
            cacheDataRepository.save(cacheData);
        }

        return byteArray;
    }

    /**
     * Returns {@link ByteArrayOutputStream} of the {@link PdfDocument} generated from given {@code Invoice} Object
     * @param invoice Invoice Object containing data
     */
    private ByteArrayOutputStream generateByteArray(Invoice invoice) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            
            PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
            Document doc = new Document(pdfDoc).setFont(font);
            float[] pointColumnWidths = { 280F, 280F };
            
            // Cells
            Table header = new Table(pointColumnWidths);
            String seller = "Seller:\n" + invoice.getSeller() + "\n" + invoice.getSellerAddress() + "\n GSTIN: "
                    + invoice.getSellerGstin();
            header.addCell(new Cell().add(new Paragraph(seller)).setPadding(30));
            String buyer = "Buyer:\n" + invoice.getBuyer() + "\n" + invoice.getBuyerAddress() + "\n GSTIN: "
                    + invoice.getBuyerGstin();
            header.addCell(new Cell().add(new Paragraph(buyer)).setPadding(30));
            
            
            // Table
            float[] productInfoColumnWidhths = { 140, 140, 140, 140 };
            Table productinfoTable = new Table(productInfoColumnWidhths);
            productinfoTable.setTextAlignment(TextAlignment.CENTER);
            productinfoTable.addCell(new Cell().add(new Paragraph("Item")));
            productinfoTable.addCell(new Cell().add(new Paragraph("Quantity")));
            productinfoTable.addCell(new Cell().add(new Paragraph("Rate")));
            productinfoTable.addCell(new Cell().add(new Paragraph("Amount")));

            List<Item> items = invoice.getItems();
            for (Item item : items) {
                productinfoTable.addCell(new Cell().add(new Paragraph(item.getName())));
                productinfoTable.addCell(new Cell().add(new Paragraph(item.getQuantity())));
                productinfoTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getRate()))));
                productinfoTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getAmount()))));
            }
            doc.add(header);
            doc.add(productinfoTable);
            writer.close();
            pdfDoc.close();
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("pdf generated successfully..");
        return byteArrayOutputStream;
    }
}
