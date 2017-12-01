public class CertificateManager {
    public static void load() {
        String value = System.getenv("HOSTTYPE");
        boolean isUbuntu = false;
        if (value == null) {
            isUbuntu = false;
        }
        else if (value.equals("x86_64") || value.equals("x86") || value.contains("x86")) {
            isUbuntu = true;
        }
        else if (value.equals("arm") || value.contains("arm")) {
            isUbuntu = false;
        }
        else{
            isUbuntu = false;
        }

        if (isUbuntu) {
            System.setProperty("javax.net.ssl.keyStore", "/home/ryan/Schedule-It/newkeystore.jks.old");
            System.setProperty("javax.net.ssl.keyStorePassword", "scheduleit");
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        }
        else {
            System.setProperty("javax.net.ssl.keyStore", "/home/ryan/Schedule-It/certificate.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "scheduleit");
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        }
    }
}
