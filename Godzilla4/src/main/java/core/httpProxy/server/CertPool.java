package core.httpProxy.server;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.WeakHashMap;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class CertPool {
   private PrivateKey privateKey;
   private X509Certificate ca;
   private KeyPair keyPair;
   Map<String, X509Certificate> cacehep = new WeakHashMap();

   public CertPool(PrivateKey privateKey, X509Certificate ca) {
      this.privateKey = privateKey;
      this.ca = ca;

      try {
         this.keyPair = CertUtil.genKeyPair();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public SSLContext getSslContext(String host) {
      int index = true;
      int index;
      if ((index = host.indexOf(":")) != -1) {
         host = host.substring(0, index);
      }

      X509Certificate x509Certificate = null;

      try {
         x509Certificate = (X509Certificate)this.cacehep.get(host);
         if (x509Certificate == null) {
            x509Certificate = CertUtil.genCert(CertUtil.getSubject(this.ca), this.privateKey, this.ca.getNotBefore(), this.ca.getNotAfter(), this.keyPair.getPublic(), host);
            this.cacehep.put(host, x509Certificate);
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return this.getSslContext(this.keyPair.getPrivate(), x509Certificate);
   }

   private SSLContext getSslContext(PrivateKey key, X509Certificate... keyCertChain) {
      try {
         KeyStore ks = KeyStore.getInstance("jks");
         ks.load((InputStream)null, (char[])null);
         ks.setKeyEntry("key", key, new char[0], keyCertChain);
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         kmf.init(ks, new char[0]);
         SSLContext ctx = SSLContext.getInstance("TLS");
         ctx.init(kmf.getKeyManagers(), (TrustManager[])null, (SecureRandom)null);
         return ctx;
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }
   }
}
