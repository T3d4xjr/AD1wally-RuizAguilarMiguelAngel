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
    
    
    // Crear una imagen procesada con las mismas dimensiones que la imagen original
    BufferedImage imagenProcesada = new BufferedImage(imagenNivel.getWidth(), imagenNivel.getHeight(), BufferedImage.TYPE_INT_ARGB);

    // Recorrer cada píxel de la imagen
    for (int x = 0; x < imagenNivel.getWidth(); x++) {
        for (int y = 0; y < imagenNivel.getHeight(); y++) {
            // Verifica si el píxel está fuera de la zona de Wally
            if (x < ubicacionWally.x || x >= ubicacionWally.x + imagenWally.getWidth() ||
                    y < ubicacionWally.y || y >= ubicacionWally.y + imagenWally.getHeight()) {
                
                // Obtener el color del píxel
                int rgb = imagenNivel.getRGB(x, y);
                Color pixelColor = new Color(rgb);
                
                // Extraer los componentes de color
                int red = pixelColor.getRed();
                int green = pixelColor.getGreen();
                int blue = pixelColor.getBlue();
                
                // Convertir a escala de grises
                int gray = (int) ((red * 0.3) + (green * 0.5) + (blue * 0.2));
                
                // Establecer el nuevo color en la imagen procesada
                Color grayColor = new Color(gray, gray, gray);
                imagenProcesada.setRGB(x, y, grayColor.getRGB());
            } else {
                // Copiar el color original de Wally
                imagenProcesada.setRGB(x, y, imagenNivel.getRGB(x, y));
            }
        }
    }

    // Colocar a Wally en su posición original
    Graphics2D g = imagenProcesada.createGraphics();
    g.drawImage(imagenWally, ubicacionWally.x, ubicacionWally.y, null);
    g.dispose();

    return imagenProcesada;
    }
}
