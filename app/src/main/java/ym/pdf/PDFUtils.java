package ym.pdf;

/**
 * @author : Gavin.GaoTJ 18.06.2019
 * @description :
 */
public class PDFUtils {

    public static float[] calculateOptimalWidthAndHeight(float viewWidth, float pageWidth, float pageHeight) {

        float maxWidth = viewWidth * 2;
        float w = Math.min(maxWidth, pageWidth);
        float h = w * pageHeight/pageWidth;
        return new float[]{w, h};
    }
}
