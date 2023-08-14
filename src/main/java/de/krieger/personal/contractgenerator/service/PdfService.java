package de.krieger.personal.contractgenerator.service;

import com.lowagie.text.DocumentException;
import de.krieger.personal.contractgenerator.model.Contract;

import java.io.File;
import java.io.IOException;

public interface PdfService {
//    File renderPdf(String html) throws IOException, DocumentException;

    String parseThymeleafTemplate(Contract contract) ;
    String parseThymeleafTemplate2() ;
}


