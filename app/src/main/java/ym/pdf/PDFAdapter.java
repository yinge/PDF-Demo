package ym.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.source.AssetSource;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Gavin.GaoTJ 20.06.2019
 * @description :
 */
public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFViewHolder> {
    private PdfiumCore pdfiumCore;
    private List<PdfDocumentItem> pdfDocumentList = new ArrayList<>();
    private int totalPageCount;
    private float[] actualPageSize;
    private ISeeLastPDFListener mSeeLastPDFListener;
    private BitmapHandler handler;

    public PDFAdapter(Context context, List<String> pdfFileNameList, float viewWidth) {
        pdfiumCore = new PdfiumCore(context);
        for (String pdfFileName : pdfFileNameList) {
            try {
                PdfDocument pdfDocument = new AssetSource(pdfFileName).createDocument(context, pdfiumCore, "");
                PdfDocumentItem pdfDocumentItem = new PdfDocumentItem(pdfDocument, pdfiumCore.getPageCount(pdfDocument));
                pdfDocumentList.add(pdfDocumentItem);
                totalPageCount += pdfDocumentItem.pageCount;
            } catch (IOException e) {
            }
        }

        handler = new BitmapHandler(this);

        pdfiumCore.openPage(pdfDocumentList.get(0).pdfDocument, 0);
        int pageWidth = pdfiumCore.getPageWidth(pdfDocumentList.get(0).pdfDocument, 0);
        int pageHeight = pdfiumCore.getPageHeight(pdfDocumentList.get(0).pdfDocument, 0);
        actualPageSize = PDFUtils.calculateOptimalWidthAndHeight(viewWidth, pageWidth, pageHeight);

        startRender(0);
    }

    List<Bitmap> result = new ArrayList<>();

    public void getBitmapsFromPdf(PdfDocumentItem pdfDocumentItem, int pdfIndex) {
        for (int i = 0; i < pdfDocumentItem.pageCount; i++) {
            pdfiumCore.openPage(pdfDocumentItem.pdfDocument, i);
            Bitmap bitmap = Bitmap.createBitmap((int) actualPageSize[0], (int) actualPageSize[1], Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdfDocumentItem.pdfDocument, bitmap, i, 0, 0, bitmap.getWidth(), bitmap.getHeight());
            result.add(bitmap);
        }
        handler.sendEmptyMessage(pdfIndex);
    }

    public void startRender(int pdfIndex) {
        if (pdfIndex < 0) {
            return;
        }
        if (pdfIndex == pdfDocumentList.size()) {
            return;
        }
        Runnable runnable = () -> getBitmapsFromPdf(pdfDocumentList.get(pdfIndex), pdfIndex);
        new Thread(runnable).start();
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new PDFViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pdf, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PDFViewHolder viewHolder, int position) {
        viewHolder.ivPDF.setImageBitmap(result.get(position));
        viewHolder.position = position;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PDFViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.position;

        if (position >= totalPageCount - 1) {
            if (mSeeLastPDFListener != null) {
                mSeeLastPDFListener.onSeenLastPDF();
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public void clearResource() {
        if (result != null) {
            result.clear();
            result = null;
        }
    }

    class PDFViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPDF;
        int position;

        public PDFViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPDF = (ImageView) itemView;
        }
    }

    class PdfDocumentItem {
        PdfDocument pdfDocument;
        int pageCount;

        public PdfDocumentItem(PdfDocument pdfDocument, int pageCount) {
            this.pdfDocument = pdfDocument;
            this.pageCount = pageCount;
        }
    }

    public interface ISeeLastPDFListener {
        void onSeenLastPDF();
    }

    public void setSeeLastPDFListener(ISeeLastPDFListener listener) {
        this.mSeeLastPDFListener = listener;
    }

    private static class BitmapHandler extends Handler {
        private WeakReference<PDFAdapter> pdfAdapter;

        public BitmapHandler(PDFAdapter pdfAdapter) {
            this.pdfAdapter = new WeakReference<>(pdfAdapter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pdfAdapter.get().notifyDataSetChanged();
            pdfAdapter.get().startRender(msg.what + 1);
        }
    }
}
