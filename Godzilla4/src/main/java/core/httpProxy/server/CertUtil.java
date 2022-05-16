package core.httpProxy.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CertUtil {
   private static KeyFactory keyFactory = null;

   private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
      if (keyFactory == null) {
         keyFactory = KeyFactory.getInstance("RSA");
      }

      return keyFactory;
   }

   public static KeyPair genKeyPair() throws Exception {
      KeyPairGenerator caKeyPairGen = KeyPairGenerator.getInstance("RSA", "BC");
      caKeyPairGen.initialize(2048, new SecureRandom());
      return caKeyPairGen.genKeyPair();
   }

   public static PrivateKey loadPriKey(byte[] bts) throws NoSuchAlgorithmException, InvalidKeySpecException {
      EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bts);
      return getKeyFactory().generatePrivate(privateKeySpec);
   }

   public static PrivateKey loadPriKey(String path) throws Exception {
      return loadPriKey(Files.readAllBytes(Paths.get(path)));
   }

   public static PrivateKey loadPriKey(URI uri) throws Exception {
      return loadPriKey(Paths.get(uri).toString());
   }

   public static PrivateKey loadPriKey(InputStream inputStream) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] bts = new byte[1024];

      int len;
      while((len = inputStream.read(bts)) != -1) {
         outputStream.write(bts, 0, len);
      }

      inputStream.close();
      outputStream.close();
      return loadPriKey(outputStream.toByteArray());
   }

   public static PublicKey loadPubKey(byte[] bts) throws Exception {
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bts);
      return getKeyFactory().generatePublic(publicKeySpec);
   }

   public static PublicKey loadPubKey(String path) throws Exception {
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Files.readAllBytes(Paths.get(path)));
      return getKeyFactory().generatePublic(publicKeySpec);
   }

   public static PublicKey loadPubKey(URI uri) throws Exception {
      return loadPubKey(Paths.get(uri).toString());
   }

   public static PublicKey loadPubKey(InputStream inputStream) throws Exception {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] bts = new byte[1024];

      int len;
      while((len = inputStream.read(bts)) != -1) {
         outputStream.write(bts, 0, len);
      }

      inputStream.close();
      outputStream.close();
      return loadPubKey(outputStream.toByteArray());
   }

   public static X509Certificate loadCert(InputStream inputStream) throws CertificateException {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return (X509Certificate)cf.generateCertificate(inputStream);
   }

   public static X509Certificate loadCert(String path) throws Exception {
      return loadCert((InputStream)(new FileInputStream(path)));
   }

   public static X509Certificate loadCert(URI uri) throws Exception {
      return loadCert(Paths.get(uri).toString());
   }

   public static String getSubject(InputStream inputStream) throws Exception {
      X509Certificate certificate = loadCert(inputStream);
      List<String> tempList = Arrays.asList(certificate.getIssuerDN().toString().split(", "));
      return (String)IntStream.rangeClosed(0, tempList.size() - 1).mapToObj((i) -> {
         return (String)tempList.get(tempList.size() - i - 1);
      }).collect(Collectors.joining(", "));
   }

   public static String getSubject(X509Certificate certificate) throws Exception {
      List<String> tempList = Arrays.asList(certificate.getIssuerDN().toString().split(", "));
      return (String)IntStream.rangeClosed(0, tempList.size() - 1).mapToObj((i) -> {
         return (String)tempList.get(tempList.size() - i - 1);
      }).collect(Collectors.joining(", "));
   }

   public static X509Certificate genCert(String issuer, PrivateKey caPriKey, Date caNotBefore, Date caNotAfter, PublicKey serverPubKey, String... hosts) throws Exception {
      String subject = (String)Stream.of(issuer.split(", ")).map((item) -> {
         String[] arr = item.split("=");
         return "CN".equals(arr[0]) ? "CN=" + hosts[0] : item;
      }).collect(Collectors.joining(", "));
      JcaX509v3CertificateBuilder jv3Builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), BigInteger.valueOf(System.currentTimeMillis() + (long)(Math.random() * 10000.0) + 1000L), caNotBefore, caNotAfter, new X500Name(subject), serverPubKey);
      GeneralName[] generalNames = new GeneralName[hosts.length];

      for(int i = 0; i < hosts.length; ++i) {
         generalNames[i] = new GeneralName(2, hosts[i]);
      }

      GeneralNames subjectAltName = new GeneralNames(generalNames);
      jv3Builder.addExtension(Extension.subjectAlternativeName, false, subjectAltName);
      ContentSigner signer = (new JcaContentSignerBuilder("SHA256WithRSAEncryption")).build(caPriKey);
      return (new JcaX509CertificateConverter()).getCertificate(jv3Builder.build(signer));
   }

   public static X509Certificate genCACert(String subject, Date caNotBefore, Date caNotAfter, KeyPair keyPair) throws Exception {
      JcaX509v3CertificateBuilder jv3Builder = new JcaX509v3CertificateBuilder(new X500Name(subject), BigInteger.valueOf(System.currentTimeMillis() + (long)(Math.random() * 10000.0) + 1000L), caNotBefore, caNotAfter, new X500Name(subject), keyPair.getPublic());
      jv3Builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));
      ContentSigner signer = (new JcaContentSignerBuilder("SHA256WithRSAEncryption")).build(keyPair.getPrivate());
      return (new JcaX509CertificateConverter()).getCertificate(jv3Builder.build(signer));
   }

   static {
      Security.addProvider(new BouncyCastleProvider());
   }
}
