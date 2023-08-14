package de.krieger.personal.contractgenerator.service;

import de.krieger.personal.contractgenerator.model.Contract;
import rst.pdfbox.layout.elements.Document;

import java.io.IOException;

public interface PdfLayoutService {
    Document createPdf(Contract contract) throws IOException;
}

