import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class BuscandoAWally {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int nivel;

        // Validación del número de nivel entre 1 y 10
        do {
            System.out.print("Ingresa un número de nivel (1-10): ");
            nivel = scanner.nextInt();
        } while (nivel < 1 || nivel > 10);

        try {
            procesarNivel(nivel);
            System.out.println("Imagen del nivel " + nivel + " procesada correctamente.");
        } catch (IOException e) {
            System.out.println("Error al procesar el nivel: " + e.getMessage());
        }
    }

    private static void procesarNivel(int nivel) throws IOException {
        // Rutas para la imagen del nivel y la imagen de Wally
        String rutaNivel = "Niveles/" + nivel + ".png";
        String rutaWally = "Niveles/" + nivel + "-wally.png";
        String rutaResultado = "ResultadoNiveles/nivel-" + nivel + "-resultado.png";

        File archivoNivel = new File(rutaNivel);
        File archivoWally = new File(rutaWally);

        // Verificar si la imagen del nivel existe
        if (!archivoNivel.exists() || !archivoWally.exists()) {
            System.out.println("No se encontró la imagen para el nivel " + nivel + ".");
            return;
        }

        // Cargar las imágenes
        BufferedImage imagenNivel = ImageIO.read(archivoNivel);
        BufferedImage imagenWally = ImageIO.read(archivoWally);

        // Localizar a Wally en la imagen del nivel
        Point ubicacionWally = encontrarWally(imagenNivel, imagenWally);
        if (ubicacionWally != null) {
            // Aplicar los efectos a la imagen completa del nivel
            BufferedImage imagenProcesada = aplicarEfectos(imagenNivel, imagenWally, ubicacionWally);

            // Guardar la imagen resultante en la carpeta ResultadoNiveles
            ImageIO.write(imagenProcesada, "png", new File(rutaResultado));
        } else {
            System.out.println("No se encontró a Wally en el nivel " + nivel + ".");
        }
    }

    private static Point encontrarWally(BufferedImage imagenNivel, BufferedImage imagenWally) {
        int anchoNivel = imagenNivel.getWidth();
        int altoNivel = imagenNivel.getHeight();
        int anchoWally = imagenWally.getWidth();
        int altoWally = imagenWally.getHeight();

        // Recorrer la imagen del nivel para buscar coincidencia de píxeles
        for (int y = 0; y <= altoNivel - altoWally; y++) {
            for (int x = 0; x <= anchoNivel - anchoWally; x++) {
                if (coincide(imagenNivel, imagenWally, x, y)) {
                    return new Point(x, y);  // Retorna la posición de Wally si coincide
                }
            }
        }
        return null; // No se encontró a Wally
    }

    private static boolean coincide(BufferedImage imagenNivel, BufferedImage imagenWally, int startX, int startY) {
        int anchoWally = imagenWally.getWidth();
        int altoWally = imagenWally.getHeight();

        // Compara píxel por píxel la imagen de Wally con la imagen del nivel
        for (int y = 0; y < altoWally; y++) {
            for (int x = 0; x < anchoWally; x++) {
                int rgbNivel = imagenNivel.getRGB(startX + x, startY + y);
                int rgbWally = imagenWally.getRGB(x, y);
                if (rgbNivel != rgbWally) {
                    return false;  // Devuelve falso si encuentra una diferencia
                }
            }
        }
        return true;
    }

    private static BufferedImage aplicarEfectos(BufferedImage imagenNivel, BufferedImage imagenWally, Point ubicacionWally) {
        BufferedImage imagenProcesada = new BufferedImage(imagenNivel.getWidth(), imagenNivel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagenProcesada.createGraphics();
        g.drawImage(imagenNivel, 0, 0, null);

        // Aplicar efecto blanco y negro y reducir brillo
        for (int y = 0; y < imagenNivel.getHeight(); y++) {
            for (int x = 0; x < imagenNivel.getWidth(); x++) {
                // Verifica si el píxel está fuera de la zona de Wally
                if (x < ubicacionWally.x || x >= ubicacionWally.x + imagenWally.getWidth() ||
                        y < ubicacionWally.y || y >= ubicacionWally.y + imagenWally.getHeight()) {
                    int rgb = imagenNivel.getRGB(x, y);

                    // Convertir a escala de grises con reducción de brillo
                    int rojo = (rgb >> 16) & 0xff;
                    int verde = (rgb >> 8) & 0xff;
                    int azul = rgb & 0xff;
                    int gris = (int) (0.3 * rojo + 0.5 * verde + 0.2 * azul);
                    gris = Math.max(gris - 50, 0);  // Reduce el brillo

                    // Establece el nuevo color en la imagen procesada
                    int nuevoColor = (0xff << 24) | (gris << 16) | (gris << 8) | gris;
                    imagenProcesada.setRGB(x, y, nuevoColor);
                }
            }
        }

        // Colocar a Wally en su posición original
        g.drawImage(imagenWally, ubicacionWally.x, ubicacionWally.y, null);
        g.dispose();

        return imagenProcesada;
    }
}
