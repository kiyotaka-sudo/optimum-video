package com.optimum.optimum.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InvoicePdfService {
    @Value("${optimum.signature.pkcs12.path:}")
    private String certificatePath;

    @Value("${optimum.signature.pkcs12.password:}")
    private String certificatePassword;

    public byte[] generateInvoice(String invoiceId, String customerEmail) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDDocumentInformation info = new PDDocumentInformation();
            info.setTitle("Facture Optimum Video " + invoiceId);
            info.setAuthor("Optimum Video");
            info.setSubject("Facture abonnement streaming");
            document.setDocumentInformation(info);

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                write(content, bold, 24, 70, 760, "Optimum Video");
                write(content, regular, 12, 70, 732, "Streaming de films camerounais et africains");
                write(content, bold, 18, 70, 690, "Facture #" + invoiceId);
                write(content, regular, 12, 70, 662, "Date: " + LocalDate.now());
                write(content, regular, 12, 70, 640, "Client: " + customerEmail);
                write(content, regular, 12, 70, 600, "Plan: Famille partagee");
                write(content, regular, 12, 70, 578, "Montant: 19.99 EUR");
                write(content, regular, 12, 70, 556, "Acces: catalogue africain, chaines TV, profils partages, telechargements offline");
                write(content, bold, 12, 70, 510, signatureStatus());
                write(content, regular, 10, 70, 488, "Trace: " + UUID.randomUUID());
            }

            if (certificatePath != null && !certificatePath.isBlank() && certificatePassword != null) {
                try {
                    java.security.KeyStore keystore = java.security.KeyStore.getInstance("PKCS12");
                    java.io.InputStream is;
                    if (certificatePath.startsWith("classpath:")) {
                        is = getClass().getResourceAsStream("/" + certificatePath.substring(10));
                    } else {
                        is = new java.io.FileInputStream(certificatePath);
                    }
                    keystore.load(is, certificatePassword.toCharArray());
                    
                    PdfSignatureService signature = new PdfSignatureService(keystore, certificatePassword, "optimum");
                    
                    org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature pdSignature = new org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature();
                    pdSignature.setFilter(org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature.FILTER_ADOBE_PPKLITE);
                    pdSignature.setSubFilter(org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
                    pdSignature.setName("Optimum Video");
                    pdSignature.setLocation("Douala, CM");
                    pdSignature.setReason("Facture officielle");
                    pdSignature.setSignDate(java.util.Calendar.getInstance());

                    document.addSignature(pdSignature, signature);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'application de la signature: " + e.getMessage());
                }
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de generer la facture PDF", exception);
        }
    }

    private void write(PDPageContentStream content, PDType1Font font, int size, float x, float y, String text) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private String signatureStatus() {
        if (certificatePath == null || certificatePath.isBlank()) {
            return "Signature numerique: certificat PKCS#12 non configure";
        }
        return "Signature numerique: certificat configure (" + certificatePath + ")";
    }
}
