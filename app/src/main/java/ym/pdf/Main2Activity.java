package ym.pdf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements PDFAdapter.ISeeLastPDFListener {

    private ZoomRecyclerView recyclerView;
    private TextView mButton;
    PDFAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mButton = findViewById(R.id.btn);
        recyclerView = findViewById(R.id.lv_pdf);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initRecyclerView();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEnableScale(true);

        List<String> pdfFileNameList = new ArrayList<>();
        pdfFileNameList.add("sample1.pdf");
        pdfFileNameList.add("sample2.pdf");
        pdfFileNameList.add("sample2.pdf");

        int viewWidth = recyclerView.getWidth() - recyclerView.getPaddingLeft() - recyclerView.getPaddingRight();
        adapter = new PDFAdapter(this, pdfFileNameList, viewWidth);
        adapter.setSeeLastPDFListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.clearResource();
        }
    }

    @Override
    public void onSeenLastPDF() {
        if (!mButton.isEnabled()) {
            mButton.setEnabled(true);
        }
    }

    @Override
    public void renderFirstSuccessful() {
        Toast.makeText(this, "渲染完成", Toast.LENGTH_SHORT).show();
    }

}
