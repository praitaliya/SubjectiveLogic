package com.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.Model.DiseaseModel;

/**
 * A dirty simple program that reads an Excel file.
 * 
 * @author Prashant
 *
 */
public class ExcelReader {
	String excelFilePath = "C:\\Users\\Prashant\\Desktop\\Study\\AI\\Project\\SL-DSS\\src\\EWEMSDataExport.xlsx";
	FileInputStream inputStream = null;
	Workbook workbook = null;
	Sheet dataSheet = null;
	Iterator<Row> iterator = null;

	public ArrayList<DiseaseModel> importData() throws IOException {
		ArrayList<DiseaseModel> data = new ArrayList<DiseaseModel>();
		inputStream = new FileInputStream(new File(excelFilePath));
		System.out.println("get stream done");
		workbook = new XSSFWorkbook(inputStream);
		dataSheet = workbook.getSheetAt(0);
		iterator = dataSheet.iterator();
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() == 0) {
				continue;
			} else {
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				while (cellIterator.hasNext()) {
					int callID = 0;
					String primaryDisease = "";
					String secondaryDisease = "";
					int age = 0;
					String ageGroup = "";
					String gender = "";
					DiseaseModel dm = new DiseaseModel();
					try {
						callID = (int) cellIterator.next().getNumericCellValue();
					} catch (Exception e) {
						callID = 0;
					}
					try {
						primaryDisease = (String) cellIterator.next().getStringCellValue();
						if(primaryDisease.equals("N/A")) {
							primaryDisease = "";
						}
					} catch (Exception e) {
						primaryDisease = "";
					}
					try {
						secondaryDisease = (String) cellIterator.next().getStringCellValue();
						if(secondaryDisease.equals("N/A")) {
							secondaryDisease = "";
						}
					} catch (Exception e) {
						secondaryDisease = "";
					}
					try {
						age = (int) cellIterator.next().getNumericCellValue();
						if(age>0 && age<=14) {
							ageGroup = "Children";
						}else if(age>=15 && age<=24) {
							ageGroup = "Youth";
						}else if(age>=25 && age<=64) {
							ageGroup = "Adults";
						}else if(age>=65) {
							ageGroup = "Seniors";
						}else {
							ageGroup = "";
						}
					} catch (Exception e) {
						age = 0;
						ageGroup = "";
					}
					try {
						gender = (String) cellIterator.next().getStringCellValue();
						if(gender.equals("N/A")) {
							gender = "";
						}
					} catch (Exception e) {
						gender = "";
					}
					dm.setCallID(callID);
					dm.setPrimaryDisease(primaryDisease);
					dm.setSecondaryDisease(secondaryDisease);
					dm.setAge(age);
					dm.setAgeGroup(ageGroup);
					dm.setGender(gender);
					data.add(dm);
				}
			}
		}
		workbook.close();
		inputStream.close();
		return data;
	}
}
