package app.services;

import app.model.Todo;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.List;


//@Service, indicates that it is a Spring-managed component
@Service
public class ExcelGeneratorService {

    public Workbook createExcelFile(List<Todo> todos) {
        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet("Todo items");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Todo List...#" + todos.size());

        XSSFCellStyle style = wb.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setBorderBottom(BorderStyle.THIN);

        XSSFFont font = wb.createFont();
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBold(true);
        style.setFont(font);

        cell.setCellStyle(style);

        //Start from second row of the Excel file since the first is the title
        int rowNumber = 2;

        for (Todo todo : todos) {
            row = sheet.createRow(rowNumber);

            cell = row.createCell(0);
            cell.setCellValue(todo.getTitle());

            cell = row.createCell(1);
            cell.setCellValue(todo.getBody());

            rowNumber++;
        }

        return wb;
    }
}