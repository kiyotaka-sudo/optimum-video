package com.optimum.optimum.service;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.InputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class PdfSignatureService implements SignatureInterface {

    private final PrivateKey privateKey;
    private final Certificate[] certificateChain;

    public PdfSignatureService(KeyStore keystore, String password, String alias) throws Exception {
        this.privateKey = (PrivateKey) keystore.getKey(alias, password.toCharArray());
        this.certificateChain = keystore.getCertificateChain(alias);
    }

    @Override
    public byte[] sign(InputStream content) throws IOException {
        try {
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            X509Certificate cert = (X509Certificate) certificateChain[0];
            ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);

            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, cert));
            gen.addCertificates(new JcaCertStore(Arrays.asList(certificateChain)));

            CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
            CMSSignedData signedData = gen.generate(msg, false);

            return signedData.getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la signature cryptographique du PDF", e);
        }
    }

    // Helper class for BouncyCastle CMSTypedData
    private static class CMSProcessableInputStream implements org.bouncycastle.cms.CMSTypedData {
        private final InputStream in;

        public CMSProcessableInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public org.bouncycastle.asn1.ASN1ObjectIdentifier getContentType() {
            return new org.bouncycastle.asn1.ASN1ObjectIdentifier("1.2.840.113549.1.7.1"); // PKCS7 data
        }

        @Override
        public void write(java.io.OutputStream out) throws IOException {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        @Override
        public Object getContent() {
            return in;
        }
    }
}
