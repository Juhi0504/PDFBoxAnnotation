package org.example;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;

public class Annotation {

    public void testPDFHighlight() throws IOException {
        File file = new File("BaseImage.pdf");
        PDDocument doc = null;
        try {
            doc = Loader.loadPDF(file);
            PDPage pdPage = doc.getPage(0);

            /*PDPageContentStream contentStream = new PDPageContentStream(doc, pdPage, AppendMode.APPEND, false);
            //Setting the non stroking color
            contentStream.setNonStrokingColor(Color.ORANGE);
            //Drawing a rectangle
            contentStream.addRect(100, 200, 200, 100);
            //Drawing a rectangle
            contentStream.fill();
            contentStream.close();*/


            List annotations = pdPage.getAnnotations();
            PDAnnotationHighlight highlight = new PDAnnotationHighlight();
            PDColor red = new PDColor(new float[] { 1, 0, 0 }, PDDeviceRGB.INSTANCE);
            highlight.setColor(red);
            highlight.setConstantOpacity((float)0.2);   // Make the highlight 20% transparent

            // Set the rectangle containing the markup
            PDRectangle position = new PDRectangle();

            position.setLowerLeftX(100);
            position.setLowerLeftY(200);
            position.setUpperRightX(400);
            position.setUpperRightY(300);
            highlight.setRectangle(position);

            // work out the points forming the four corners of the annotations
            // set out in anti clockwise form (Completely wraps the text)
            // OK, the below doesn't match that description.
            // It's what acrobat 7 does and displays properly!
            float[] quads = new float[8];

            quads[0] = position.getLowerLeftX();  // x1
            quads[1] = position.getUpperRightY()-2; // y1
            quads[2] = position.getUpperRightX(); // x2
            quads[3] = quads[1]; // y2
            quads[4] = quads[0];  // x3
            quads[5] = position.getLowerLeftY()-2; // y3
            quads[6] = quads[2]; // x4
            quads[7] = quads[5]; // y5

            highlight.setQuadPoints(quads);
            highlight.setReadOnly(true);
            highlight.setNoRotate(true);
            highlight.setLocked(true);
            highlight.constructAppearances(doc);
            annotations.add(highlight);

            doc.save("BaseImageHighlighted.pdf");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally
        {
            doc.close();
        }

    }
    public void addAnnotations() throws IOException {
        File file = new File("source.pdf");
        PDDocument doc = Loader.loadPDF(file);

        try {
            PDPage pdPage = doc.getPage(0);
            List annotations = pdPage.getAnnotations();

            addHighlight(doc, pdPage, annotations);
            addRectangleWithText(doc, pdPage);
            //addText(doc, pdPage);
            addImage(doc, pdPage);

            addRubberStamp(doc, pdPage, annotations);
            //addAnnotationWithRandomImage(doc, pdPage, annotations);

            doc.save( "destination.pdf" );

        } catch (IOException e) {
            System.out.println("Exception encountered= " + e);
        }
        finally
        {
            doc.close();
        }
    }

    private void addAnnotationWithRandomImage(PDDocument doc, PDPage page, List<PDAnnotation> annotations) throws IOException {
        PDAnnotationCircle annotationCircle = new PDAnnotationCircle();

        // Get the appearance stream of the annotation
        PDAppearanceStream appearanceStream = annotationCircle.getNormalAppearanceStream();
        if (appearanceStream == null) {
            appearanceStream = new PDAppearanceStream(doc);
            annotationCircle.setAppearance(new PDAppearanceDictionary(appearanceStream.getCOSObject()));
        }
        if (appearanceStream.getResources() == null) {
            appearanceStream.setResources(new PDResources());
        }
        // Set the width and height of the appearance stream
        appearanceStream.setBBox(new PDRectangle(100, 100)); // Set the width and height according to your image size


        PDImageXObject pdImage = PDImageXObject.createFromFile("check.jpg", doc);

        PDPageContentStream contentStream = new PDPageContentStream(doc, appearanceStream);
        //contentStream.setGraphicsStateParameters(new PDExtendedGraphicsState());
        contentStream.drawImage(pdImage, 170, 654, 30, 22);

        contentStream.close();

        annotations.add(annotationCircle);
    }

    private void addImage(PDDocument document, PDPage page) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);
        PDImageXObject pdImage = PDImageXObject.createFromFile("check.jpg", document);
        contentStream.drawImage(pdImage, 500, 660, 18, 15);
        contentStream.close();
    }

    private void addText(PDDocument document, PDPage page) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);

        //Begin the Content stream
        contentStream.beginText();

        FontName font_name_3v= Standard14Fonts.getMappedFontName("HELVETICA_BOLD");
        PDFont pdfFont=  new PDType1Font(font_name_3v.HELVETICA_BOLD);
        //Setting the font to the Content stream
        contentStream.setFont(pdfFont, 12);

        //Setting the position for the line
        contentStream.newLineAtOffset(25, 500);

        String text = "This is the sample document and we are adding content to it.";

        //Adding text in the form of string
        contentStream.showText(text);

        //Ending the content stream
        contentStream.endText();

        System.out.println("Content added");

        //Closing the content stream
        contentStream.close();
    }
    private void addRectangleWithText(PDDocument document, PDPage page) throws IOException {
        //Instantiating the PDPageContentStream class
        PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, false);
        //Setting the non stroking color
        contentStream.setNonStrokingColor(Color.ORANGE);
        //Drawing a rectangle
        contentStream.addRect(200, 510, 100, 20);
        //Drawing a rectangle
        contentStream.fill();
        System.out.println("rectangle added");

        FontName font_name_3v= Standard14Fonts.getMappedFontName("HELVETICA_BOLD");
        PDFont pdfFont=  new PDType1Font(font_name_3v.HELVETICA_BOLD);
        contentStream.setNonStrokingColor(Color.BLACK);
        //Setting the font to the Content stream
        contentStream.setFont(pdfFont, 10);
        //Setting the position for the line
        contentStream.beginText();
        contentStream.newLineAtOffset(230, 515);
        String text = "SUP";
        //Adding text in the form of string
        contentStream.showText(text);
        //Ending the content stream
        contentStream.endText();
        System.out.println("Content added");

        //Closing the ContentStream object
        contentStream.close();
    }

    private void addRubberStamp(PDDocument doc, PDPage page, List<PDAnnotation> annotations) throws IOException {
        // Now add the stamp annotation
        PDAnnotationRubberStamp rubberStamp = new PDAnnotationRubberStamp();
        rubberStamp.setName(PDAnnotationRubberStamp.NAME_APPROVED);
        rubberStamp.setContents("A top secret note");
        PDRectangle rectangle = new PDRectangle(170, 654, 30, 22);
        rubberStamp.setRectangle(rectangle);

        // Get the appearance stream of the rubber stamp
        PDAppearanceStream appearanceStream = rubberStamp.getNormalAppearanceStream();
        if (appearanceStream == null) {
            appearanceStream = new PDAppearanceStream(doc);
            rubberStamp.setAppearance(new PDAppearanceDictionary(appearanceStream.getCOSObject()));
        }
        if (appearanceStream.getResources() == null) {
            appearanceStream.setResources(new PDResources());
        }
        // Create a PDXObjectImage with the given jpg
        PDPageContentStream contentStream = new PDPageContentStream(doc, appearanceStream);
        //Setting the non stroking color
        contentStream.setNonStrokingColor(Color.GREEN);
        //Drawing a rectangle
        contentStream.addRect(170, 654, 30, 22);
        //Drawing a rectangle
        contentStream.fill();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 1);
        //Setting the font to the Content stream
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        //contentStream.showText("NA");

        //Ending the content stream text
        contentStream.endText();

        // Create a content stream for the appearance stream
        // Close the content stream
        contentStream.close();
        rubberStamp.setReadOnly(true);
        rubberStamp.setNoRotate(true);
        rubberStamp.setLocked(true);
        //Add the new RubberStamp to the document
        annotations.add(rubberStamp);
        rubberStamp.constructAppearances(doc);
    }

    private void addHighlight(PDDocument doc, PDPage pdPage, List<PDAnnotation> annotations) throws IOException {
        // Now add the highlight annotation, a highlight to PDFBox text
        PDAnnotationHighlight highlight = new PDAnnotationHighlight();
        PDColor red = new PDColor(new float[] { 1, 0, 0 }, PDDeviceRGB.INSTANCE);
        highlight.setColor(red);
        highlight.setConstantOpacity((float)0.2);   // Make the highlight 20% transparent

        // Set the rectangle containing the markup
        PDFont font = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
        float textWidth = (font.getStringWidth( "Employer's name," )/1000) * 18;
        PDRectangle position = new PDRectangle();
        float inch = 40;
        float ph = pdPage.getMediaBox().getUpperRightY();

        position.setLowerLeftX(inch);
        position.setLowerLeftY( ph-125 );
        position.setUpperRightX(inch + textWidth);
        position.setUpperRightY(ph-90);
        highlight.setRectangle(position);

        // work out the points forming the four corners of the annotations
        // set out in anti clockwise form (Completely wraps the text)
        // OK, the below doesn't match that description.
        // It's what acrobat 7 does and displays properly!
        float[] quads = new float[8];

        quads[0] = position.getLowerLeftX();  // x1
        quads[1] = position.getUpperRightY()-2; // y1
        quads[2] = position.getUpperRightX(); // x2
        quads[3] = quads[1]; // y2
        quads[4] = quads[0];  // x3
        quads[5] = position.getLowerLeftY()-2; // y3
        quads[6] = quads[2]; // x4
        quads[7] = quads[5]; // y5

        highlight.setQuadPoints(quads);
        highlight.setContents("Highlighted since it's important");
        highlight.setReadOnly(true);
        highlight.setNoRotate(true);
        highlight.setLocked(true);
        highlight.constructAppearances(doc);
        annotations.add(highlight);

    }
}
