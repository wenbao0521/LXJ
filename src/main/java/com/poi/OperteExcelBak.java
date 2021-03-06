package com.poi;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wwb
 * @title: OperteExcel
 * @projectName aliyun-zhibo
 * @description: TODO
 * @date 2020/1/198:57
 */
public class OperteExcelBak {
    public String importExcel(String filePath) throws Exception {
        final  String EXCEL_XLS = "xls";
        final  String EXCEL_XLSX = "xlsx";

        //判断文件
        if (filePath != null && !"".equals(filePath)) {
            File file = new File(filePath);
            //判断格式
            if (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)) {
                //创建输入流对象
                InputStream is = new FileInputStream(file);
                Workbook workbook = null;
                //判断excel版本号
                if (file.getName().endsWith(EXCEL_XLS)) {
                    workbook = new HSSFWorkbook(is);
                } else if (file.getName().endsWith(EXCEL_XLSX)) {
                    workbook = new XSSFWorkbook(is);
                }
                Map<String, Object> objectMap=new HashMap<>();
                //循环表格（sheet）
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    //判断sheet是否有数据
                    if (sheet.getPhysicalNumberOfRows() <= 0) {
                        continue;
                    }
                    //存放集合
                    List<Map<String, Object>> list = new ArrayList<>();
                    //存放表头名字（第一行的数据）
                    List<String> header = new ArrayList<>();
                    for (int x = 0; x < sheet.getRow(0).getLastCellNum(); x++) {
                        Cell cell = sheet.getRow(0).getCell(x);
                        String value = cell.getStringCellValue();
                        header.add(value);
                    }

                    //获取行并进行循环
                    for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                        Row row = sheet.getRow(j);
                        //判断row是否有数据
                        if (row.getPhysicalNumberOfCells() <= 0) {
                            continue;
                        }
                        //存放数据
                        Map<String, Object> map = new HashMap<>();
                        //获取单元格并进行循环
                        for (int k = 0; k < row.getLastCellNum(); k++) {
                            Cell cell = row.getCell(k);
                            if (cell == null || cell.toString().trim().equals("")) {
                                continue;
                            }
                            CellType cellType = cell.getCellTypeEnum();
                            //存放值
                            String cellValue = "";
                            //字符串
                            if (cellType == CellType.STRING) {
                                cellValue = cell.getStringCellValue().trim();
                                cellValue = (cellValue==null || "".equals(cellValue)) ? "" : cellValue;
                            }
                            //数据格式
                            if (cellType == CellType.NUMERIC) {
                                //判断日期类型
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String date1 = dff.format(cell.getDateCellValue());
                                    cellValue = date1;
                                } else {
                                    //设置数据格式（"#.######"是几位小数）
                                    cellValue = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                                }
                            }
                            if (cellType == CellType.BOOLEAN) {
                                cellValue = String.valueOf(cell.getBooleanCellValue());
                            }
                            //添加数据到map
                            map.put(header.get(k), cellValue);
                        }


                        int writeTemp = row.getLastCellNum();
                        if(row.getCell(writeTemp) == null){
                            Cell writeCell123 = row.createCell(writeTemp);

//                            String w = "E"+(row.getLastCellNum()+1);
//                            System.out.println(w);
//                            //获取单元格的row和cell
//                            CellAddress address = new CellAddress(w);
////                        // 获取行
//                            Row writeRow = sheet.getRow(address.getRow());
//                            // 获取列
//                            Cell writeCell = writeRow.getCell(address.getColumn());
//                        设置单元的值
//                            writeCell.setCellValue("122222");
                            writeCell123.setCellValue("122222");
                            //写入数据
                            FileOutputStream excelFileOutPutStream = new FileOutputStream(filePath);
                            workbook.write(excelFileOutPutStream);
                            excelFileOutPutStream.flush();
                            excelFileOutPutStream.close();
                            System.out.println("指定单元格设置数据写入完成");
                        }






                        //把map数据添加到list
                        list.add(map);
                    }
                    objectMap.put(sheet.getSheetName(),list);
                }
                return objectMap.toString();
            } else {
                return "文件不是excel";
            }
        } else {
            return "文件不存在";
        }
    }

    public static void main(String[] args) throws Exception {
        OperteExcelBak operateExcel = new OperteExcelBak();
        System.out.println(operateExcel.importExcel("D:\\wwb\\Docs\\直播导播\\直播管控权限--按钮.xlsx" ));
    }
}



