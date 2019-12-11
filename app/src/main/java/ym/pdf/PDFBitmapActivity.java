package ym.pdf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.github.barteksc.pdfviewer.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PDFBitmapActivity extends AppCompatActivity {

    private ListView pdfList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfbitmap);

        try {
            File file = FileUtils.fileFromAsset(this, "sample1.pdf");
            ArrayList<Bitmap> bitmaps = pdfToBitmap(file);
            System.out.println("bitmaps: " + bitmaps.size());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("bitmaps: error" );
        }
    }

    /**
     *
     * @param pdfFile
     * @return
     */
    private ArrayList<Bitmap> pdfToBitmap(File pdfFile) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                Bitmap bitmap;
                final int pageCount = renderer.getPageCount();
                Log.e("test_sign", "图片de 张数： " +pageCount);
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    int width = getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                    int height = getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    //todo 以下三行处理图片存储到本地出现黑屏的问题，这个涉及到背景问题
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    Rect r = new Rect(0, 0, width, height);
                    page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    bitmaps.add(bitmap);
                    // close the page
                    page.close();
                }
                // close the renderer
                renderer.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmaps;

    }
}
