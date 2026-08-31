package com.rianascorp.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;


public class ImageUtils {

    // Standard sizes for photos
    public static final int STANDARD_WIDTH=400;
    public static final int STANDARD_HEIGHT=400;
    public static final int THUMBNAIL_WIDTH=100;
    public static final int THUMBNAIL_HEIGHT=100;
    public static final long MAX_FILE_SIZE=5*1024*1024;

    //Resize methods

    /** resize image to standard dimensions
     */

    public static byte[] resizeToStandard(byte[] imageBytes) throws IOException{
        return resizeImage(imageBytes, STANDARD_WIDTH, STANDARD_HEIGHT);
    }

    /** resize image to custom dimensions
     */

    public static byte[] resizeImage(byte[] imageBytes, int targetWidth, int targetHeight) throws IOException {

        if (imageBytes == null || imageBytes.length == 0) {
            return null;
        }

        // Read image
        BufferedImage original;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            original = ImageIO.read(bais);
        }

        if (original == null) {
            throw new IOException("Unsupported image format");
        }

        int width = original.getWidth();
        int height = original.getHeight();
        // If image is already smaller than target, keep original size
        if (width <= targetWidth && height <= targetHeight) {
           return imageBytes;
        }


        // Calculate new dimensions maintaining aspect ratio
        double ratio = Math.min((double) targetWidth / width, (double) targetHeight / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);


        // Create resized image
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        // Convert back to bytes
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(resized, "jpg", baos);
            if (!success) {
                throw new IOException("Failed to write image as JPG");
            }
            byte[] result = baos.toByteArray();
          return result;
        }
    }

    /**
     *Create a thumbnail (useful for list views)
     */

    public static byte[] createThumbnail(byte[] imageBytes) throws IOException{
        if (imageBytes == null || imageBytes.length == 0) {
            return null;
        }
        BufferedImage original;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            original = ImageIO.read(bais);
        }
        if (original == null) {
            return null;
        }
        //Create square thumbnail (crop center)
        int width=original.getWidth();
        int height=original.getHeight();
        int size=Math.min(width,height);
        int x=(width-size)/2;
        int y=(height-size)/2;

        BufferedImage cropped=original.getSubimage(x,y,size,size);

        //Resize to thumbnail size
        BufferedImage thumbnail=new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g=thumbnail.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(cropped,0,0,THUMBNAIL_WIDTH,THUMBNAIL_HEIGHT,null);
        g.dispose();
        try(ByteArrayOutputStream baos=new ByteArrayOutputStream()){
            ImageIO.write(thumbnail, "jpg", baos);
            return baos.toByteArray();
        }
    }

        // Convert File to byte[]
        public static byte[] fileToBytes(File file) throws IOException {
            try (FileInputStream fis = new FileInputStream(file);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                return bos.toByteArray();
            }
        }

        public static byte[] fileToBytes (File file, boolean resize) throws IOException{
        byte[] bytes=fileToBytes(file);
        if (resize){
            return resizeToStandard(bytes);
        }
        return  bytes;
        }

        // Convert byte[] to File
        public static void bytesToFile(byte[] bytes, File outputFile) throws IOException {
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(bytes);
            }
        }

        // Convert Image to byte[]
        public static byte[] imageToBytes(Image image) throws IOException {
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();

            // Create a BufferedImage
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Read pixels from JavaFX Image
            PixelReader reader = image.getPixelReader();
            WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraInstance();
            byte[] pixels = new byte[width * height * 4];
            reader.getPixels(0, 0, width, height, format, pixels, 0, width * 4);

            // Convert to BufferedImage
            ByteBuffer buffer = ByteBuffer.wrap(pixels);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int b = buffer.get() & 0xFF;
                    int g = buffer.get() & 0xFF;
                    int r = buffer.get() & 0xFF;
                    int a = buffer.get() & 0xFF;
                    int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                    bufferedImage.setRGB(x, y, rgb);
                }
            }

            // Convert to byte[]
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "jpg", baos);
                return baos.toByteArray();
            }
        }

        // Convert byte[] to JavaFX Image
        public static Image bytesToImage(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                return new Image(bais);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

}
