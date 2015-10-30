package akj.apiautomation;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelParser {

	private static ExcelParser instance;

	private ExcelParser() {
	}

	public static ExcelParser getInstance() {
		if (null == instance) {
			instance = new ExcelParser();
		}
		return instance;
	}

	public List<String> readResponseFile(String fileName, String useCase) {
		System.out.println("use case:" + useCase);
		List<String> parameters = new ArrayList<String>();
		String cell;
		try {
			/**
			 * Create a new instance for FileInputStream class
			 */
			FileInputStream fileInputStream = new FileInputStream(fileName);
			XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
			XSSFSheet xssfSheet = workBook.getSheetAt(0);
			Sheet sheet = workBook.getSheetAt(0);
			System.out.println(sheet.getFirstRowNum());
			System.out.println(sheet.getLastRowNum());
			for (int i = xssfSheet.getFirstRowNum(); i <= xssfSheet
					.getLastRowNum(); i++) {
				if (xssfSheet.getRow(i).getCell(0).toString().equals(useCase)) {
					for (int j = 2; j < xssfSheet.getRow(i).getLastCellNum(); j++) {
						cell = xssfSheet.getRow(i).getCell(j).toString();
						parameters.add(cell);
					}

				}
			}
			for (int i = 0; i < parameters.size(); i++)
				System.out.println(parameters.get(i));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return parameters;
	}

	public ArrayList<ArrayList<String>> readAllRow(String fileName) {

		ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();

		try {
			/**
			 * Create a new instance for FileInputStream class
			 */
			FileInputStream fileInputStream = new FileInputStream(fileName);
			XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
			XSSFSheet xssfSheet = workBook.getSheetAt(0);
			for (int i = xssfSheet.getFirstRowNum(); i <= xssfSheet
					.getLastRowNum(); i++) {
				ArrayList<String> row = new ArrayList<String>();
				for (int j = 0; j < xssfSheet.getRow(i).getLastCellNum(); j++) {
					String cell = "";
					if (xssfSheet.getRow(i).getCell(j) != null)
						cell = xssfSheet.getRow(i).getCell(j).toString();
					row.add(cell);
				}
				parameters.add(row);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parameters;
	}
	
	public ArrayList<ArrayList<String>> readAllRow(String fileName, int sheetIndex) {

		ArrayList<ArrayList<String>> parameters = new ArrayList<ArrayList<String>>();

		try {
			/**
			 * Create a new instance for FileInputStream class
			 */
			FileInputStream fileInputStream = new FileInputStream(fileName);
			XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
			XSSFSheet xssfSheet = workBook.getSheetAt(sheetIndex);
			for (int i = xssfSheet.getFirstRowNum(); i <= xssfSheet
					.getLastRowNum(); i++) {
				ArrayList<String> rows = new ArrayList<String>();
				for (int j = 0; j < xssfSheet.getRow(i).getLastCellNum(); j++) {
					String cell = "";
					if (xssfSheet.getRow(i).getCell(j) != null)
						cell = xssfSheet.getRow(i).getCell(j).toString();
					rows.add(cell);
				}
				parameters.add(rows);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parameters;
	}

}
