package ym.pdf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        PDFView pdfView = ((PDFView)findViewById(R.id.pdfview));

        pdfView.fromAsset("sample2.pdf").pageFitPolicy(FitPolicy.WIDTH).
                load();

//        PDFAsyncTask pdfAsyncTask = new PDFAsyncTask(new AssetSource("sample1.pdf"), "", new PdfiumCore(this), 0,this);
//        pdfAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
