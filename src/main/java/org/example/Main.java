package org.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Directorio donde se guardarán los PDFs
        String downloadDirPath = System.getProperty("user.dir") + File.separator + "downloads";
        File downloadDir = new File(downloadDirPath);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        // Configuración del ChromeDriver: asegúrate de que "chromedriver.exe" esté en la ruta indicada
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // Puedes ejecutar en modo headless si lo prefieres:
        // options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        try {
            // Página principal
            String baseUrl = "https://nachoiborraies.github.io/java/";
            driver.get(baseUrl);
            // Esperar a que cargue la página principal
            Thread.sleep(2000);

            // Extraer enlaces a secciones:
            // Se buscan enlaces que contengan "md/en/" pero que NO contengan ".pdf"
            List<WebElement> sectionElements = driver.findElements(By.xpath("//a[contains(@href, 'md/en/') and not(contains(@href, '.pdf'))]"));
            System.out.println("Se encontraron " + sectionElements.size() + " enlaces de secciones.");

            // Utilizamos un Set para evitar duplicados
            Set<String> sectionUrls = new HashSet<>();
            for (WebElement elem : sectionElements) {
                String href = elem.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    sectionUrls.add(href);
                }
            }
            System.out.println("Secciones únicas: " + sectionUrls.size());

            // Para cada sección, genera la URL del PDF agregando ".pdf"
            for (String sectionUrl : sectionUrls) {
                // Quitar la barra final si existe para evitar doble "//"
                if (sectionUrl.endsWith("/")) {
                    sectionUrl = sectionUrl.substring(0, sectionUrl.length() - 1);
                }
                String pdfUrl = sectionUrl + ".pdf";
                System.out.println("Intentando descargar PDF: " + pdfUrl);

                // Extraer el nombre del archivo (la parte después del último '/')
                String fileName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
                String filePath = downloadDirPath + File.separator + fileName;

                // Descargar el archivo PDF
                downloadFile(pdfUrl, filePath);
                // Pequeña pausa entre descargas
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        System.out.println("Proceso completado. Revisa el directorio: " + downloadDirPath);
    }

    public static void downloadFile(String fileURL, String fileName) {
        try {
            FileUtils.copyURLToFile(new URL(fileURL), new File(fileName));
            System.out.println("Descargado: " + fileName);
        } catch (IOException e) {
            System.err.println("Error al descargar " + fileName + " desde " + fileURL + ": " + e.getMessage());
        }
    }
}
