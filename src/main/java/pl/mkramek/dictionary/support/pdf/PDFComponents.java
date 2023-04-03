package pl.mkramek.dictionary.support.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFComponents {
    public static Paragraph documentTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        Paragraph title = new Paragraph("Dictionary Report - %s".formatted(sdf.format(new Date())), titleFont());
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }

    public static Paragraph subtitle(String text) {
        Paragraph title = new Paragraph(text, subtitleFont());
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }

    public static PdfPTable table() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(15f);
        table.setSpacingAfter(20f);
        table.setWidths(new float[] {1f, 1.5f});
        return table;
    }

    public static PdfPCell tableHeaderCell(String text) {
        PdfPCell cell = new PdfPCell();
        cell.setPaddingTop(5f);
        cell.setPaddingBottom(10f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(2);
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setPhrase(new Phrase(text, tableHeaderFont()));
        return cell;
    }

    public static PdfPCell tableLeftCell(String text) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5f);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBackgroundColor(Color.WHITE);
        cell.setPhrase(new Phrase(text, tableLeftFont()));
        return cell;
    }

    public static PdfPCell tableRightCell(String text) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5f);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBackgroundColor(Color.WHITE);
        cell.setPhrase(new Phrase(text, tableRightFont()));
        return cell;
    }

    private static Font titleFont() {
        Font target = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE);
        target.setSize(20);
        target.setColor(Color.BLACK);
        return target;
    }

    private static Font subtitleFont() {
        Font target = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE);
        target.setSize(18);
        target.setColor(Color.BLACK);
        return target;
    }

    private static Font tableHeaderFont() {
        Font target = FontFactory.getFont(FontFactory.HELVETICA);
        target.setSize(14);
        target.setColor(Color.WHITE);
        return target;
    }

    private static Font tableLeftFont() {
        Font target = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE);
        target.setSize(12);
        target.setColor(Color.BLACK);
        return target;
    }

    private static Font tableRightFont() {
        Font target = FontFactory.getFont(FontFactory.HELVETICA);
        target.setSize(12);
        target.setColor(Color.BLACK);
        return target;
    }
}
