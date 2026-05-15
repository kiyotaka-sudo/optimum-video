package com.optimum.optimum.controller;

import com.optimum.optimum.service.InvoicePdfService;
import java.security.Principal;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions/invoices")
public class InvoiceRestController {
    private final InvoicePdfService invoicePdfService;

    public InvoiceRestController(InvoicePdfService invoicePdfService) {
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping("/{invoiceId}/pdf")
    ResponseEntity<byte[]> download(@PathVariable("invoiceId") String invoiceId, Principal principal) {
        String email = principal == null ? "client@optimum.video" : principal.getName();
        byte[] pdf = invoicePdfService.generateInvoice(invoiceId, email);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("facture-optimum-" + invoiceId + ".pdf")
                        .build()
                        .toString())
                .body(pdf);
    }
}
