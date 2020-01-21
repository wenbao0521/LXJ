//package com.poi;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.poi.hssf.usermodel.HSSFDateUtil;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.io.*;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author wwb
// * @title: OperteExcel
// * @projectName aliyun-zhibo
// * @description: TODO
// * @date 2020/1/198:57
// */
//
//@RunWith(SpringJUnit4ClassRunner.class)
////// 加载配置文件，否则无法注入
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//@SpringBootTest
//@Slf4j
//public class OperteExcel {
//    // 自动注入
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//    @Test
//    public void operateExcel() throws Exception {
//        //业务 excel 文件 地址
//        final String professionalWorkFilePath="D:\\wwb\\lxj\\1.10营业部-反馈.xlsx" ;
//        Workbook workbook = getWorkbook(professionalWorkFilePath);
//        if (workbook == null){return;}
//        Sheet sheet = workbook.getSheetAt(0);
//        //判断sheet是否有数据
//        if (sheet.getPhysicalNumberOfRows() <= 0) {return ;}
//        //获取 所有 行数据  并进行  循环
//        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
//            Row row = sheet.getRow(j);
//            //判断row是否有数据
//            if (row.getPhysicalNumberOfCells() <= 0) {
//                continue;
//            }
//            String channelStr = getCellValue(row.getCell(13));//渠道 14N
//            String insuranceTypeStr = getCellValue(row.getCell(15));//险种 16P
//            String useNatureStr = getCellValue(row.getCell(16));//使用性质17Q
//            String carTypeStr =  getCellValue(row.getCell(17));//车辆种类18R
//            String newCarPurchasePriceStr =  getCellValue(row.getCell(23));//新车购置价 24X
//            String ncdStr =  getCellValue(row.getCell(35));//NCD系数 36AJ
//            String isCarDamageStr =  getCellValue(row.getCell(32));//是否含车损 33AG
//            String isTransferStr =  getCellValue(row.getCell(34));//是否过户车 35AI
//            String isNewCarStr =  getCellValue(row.getCell(33));//是否新车 34AH
//            String writeSNoNewCarStr1 = ("经代渠道".equals(channelStr) ? "t14":"t15");
//            String writeNoNewCarStr2 = "t18";
//            String writeSNewCarStr1 = "t20";
//            String writeNewCarStr2 = "t24";
//
//
//
//            List<Map<String, Object>> baseExcelDataList  = null;
//            try {
//                Map<String,String> paramMap = new HashMap<String,String>();
//                paramMap.put("channelStr",channelStr);paramMap.put("insuranceTypeStr",insuranceTypeStr);
//                paramMap.put("useNatureStr",useNatureStr);
//                String sqlTemp = " select * from baseInfo where t2='{channelStr}' and t3='{insuranceTypeStr}' and t4='{useNatureStr}' ";
//                Map<String,String> paramsMapTemp = SqlParser.escape4select(paramMap);
//                baseExcelDataList = jdbcTemplate.queryForList(SqlParser.parse(sqlTemp, paramsMapTemp));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (CollectionUtils.isEmpty(baseExcelDataList)){
//                log.info("---baseExcelDataList---值为-->{}",baseExcelDataList);
//                log.info("channelStr-->{}--insuranceTypeStr-->{}--useNatureStr-->{}",channelStr,insuranceTypeStr,useNatureStr);
//            }
//            baseExcelDataList.forEach(baseMap -> {
//                if("家庭自用汽车".equals(useNatureStr)){
//                    if(isTransferStr.equals(baseMap.get("t9").toString())){//是否 过户 车
//                        if("是".equals(isTransferStr)){
//                            if(isCarDamageStr.equals(baseMap.get("t8").toString())){//是否含车损
//                                if("是".equals(isTransferStr)){//是过户车还含车损，没有这政策
//                                    writCellFunc(row,"是否过户车：是，是否包含车损：是","是否过户车：是，是否包含车损：是");
//                                }
//                                if("否".equals(isTransferStr)){
//                                    if("非新车".equals(isNewCarStr)){//
//                                        String noNewCarC1= baseMap.get(writeSNoNewCarStr1).toString();String noNewCarC4= baseMap.get(writeNoNewCarStr2).toString();
//                                        writCellFunc(row,noNewCarC1,noNewCarC4);
//                                    }
//                                    if("新车".equals(isNewCarStr)){//
//                                        String newCarC1= baseMap.get(writeSNewCarStr1).toString();String newCarC4= baseMap.get(writeNewCarStr2).toString();
//                                        writCellFunc(row,newCarC1,newCarC4);
//                                    }
//                                }
//                            }
//                        }
//                        if("未过户".equals(isTransferStr) ){
//                            if(baseMap.get("t8") == null){//是否过户车：未过户，是否包含车损为null的话，直接判断新旧车，和新车购价
//                                if("非新车".equals(isNewCarStr)){
//                                    writCellFunc(row,"是否过户车：未过户,基础表是否包含车损：null,是否新车：非新车","是否过户车：未过户,基础表是否包含车损：null,是否新车：非新车");
//                                }
//                                if("新车".equals(isNewCarStr)){
//                                    if(500000 > Integer.parseInt(newCarPurchasePriceStr)){
//                                        String newCarC1= baseMap.get(writeSNewCarStr1).toString();String newCarC4= baseMap.get(writeNewCarStr2).toString();
//                                        writCellFunc(row,newCarC1,newCarC4);
//                                    }else{
//                                        writCellFunc(row,"是否过户车：未过户,基础表是否包含车损：null,是否新车：新车,新车购置价<500000:否","是否过户车：未过户,基础表是否包含车损：null,是否新车：新车,新车购置价<500000:否");
//                                    }
//                                }
//                            }else{//是否含车损  不为null
//                                if(isCarDamageStr.equals(baseMap.get("t8").toString())){//是否含车损
//                                    if("是".equals(isCarDamageStr)){
////                                        情况4 1、是否过户车→否→是否新车→非新车→是否含车损→是→新车购置价→50万以内→NCD≤1.25
////                                        情况5 1、是否过户车→否→是否新车→非新车→是否含车损→是→新车购置价→50万以内→NCD＞1.25
//                                        if("非新车".equals(isNewCarStr)){
//                                            if(500000 > Integer.parseInt(newCarPurchasePriceStr)){
//                                                if(Float.parseFloat(ncdStr) <= 1.25 && "NCD≤1.25".equals(baseMap.get("t7").toString())){
//                                                    String noNewCarC1= baseMap.get(writeSNoNewCarStr1).toString();String noNewCarC4= baseMap.get(writeNoNewCarStr2).toString();
//                                                    writCellFunc(row,noNewCarC1,noNewCarC4);
//                                                }
//                                                if(Float.parseFloat(ncdStr) > 1.25 && "NCD>1.25".equals(baseMap.get("t7").toString())){
//                                                    String noNewCarC1= baseMap.get(writeSNoNewCarStr1).toString();String noNewCarC4= baseMap.get(writeNoNewCarStr2).toString();
//                                                    writCellFunc(row,noNewCarC1,noNewCarC4);
//                                                }
//                                            }
//                                        }
//                                        if("新车".equals(isNewCarStr)){
//                                            writCellFunc(row,"是否过户车：未过户，是否包含车损：是，是否新车，是,","是否过户车：未过户，是否包含车损：是，是否新车，是");
//                                        }
//                                    }
//                                    if("否".equals(isCarDamageStr)){
//                                        if("非新车".equals(isNewCarStr)){//
//                                            String noNewCarC1= baseMap.get(writeSNoNewCarStr1).toString();String noNewCarC4= baseMap.get(writeNoNewCarStr2).toString();
//                                            writCellFunc(row,noNewCarC1,noNewCarC4);
//                                        }
//                                        if("新车".equals(isNewCarStr)){//
//                                            String newCarC1= baseMap.get(writeSNewCarStr1).toString();String newCarC4= baseMap.get(writeNewCarStr2).toString();
//                                            writCellFunc(row,newCarC1,newCarC4);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }else{//非家庭自用汽车操作
//
//
//
//
//
//
//
//
//
//                }
//            });
//
//
//
//
//
//
////
////            //现在查找  家庭自用汽车
////            List<Map<String, Object>> forOwnWseCarList = baseExcelDataList.stream().filter((map) ->
////                    "家庭自用汽车".equals(useNatureStr)
////            ).collect(Collectors.toList());
//
//
//
//
//
//
//
//
////            //结果集
////            List<Map<String,Object>> resultList = baseExcelDataList.stream().filter((map)->
////                    channelStr.equals(map.get("t2").toString()) &&
////                            insuranceTypeStr.equals(map.get("t3").toString())&&
////                            useNatureStr.equals(map.get("t4").toString())&&
////                            isTransferStr.equals(map.get("t9").toString())
////            ).collect(Collectors.toList());
//
////            //现在查找 是否过户车
////            List<Map<String, Object>> isTransferStrList = forOwnWseCarList.stream().filter((map) ->
////
////            ).collect(Collectors.toList());
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////            if("新车".equals(isNewCarStr)){//如果新车 基础表信息中，是否包含车损为空 resultList "家庭自用汽车".equals(useNatureStr)&&
////                int newCarPurchasePricesInt = Integer.parseInt(newCarPurchasePriceStr);
////                if(newCarPurchasePricesInt > 500000){//如果是新车，新车购置价还比基础表中的50W小，就找到了c1和c4的值，else就说明数据不在 政策中
////                    int writeTemp = row.getLastCellNum();
////                    if(row.getCell(writeTemp) == null){
////                        Cell writeCell = row.createCell(writeTemp);
//////                        String writeContent = getCellValue(row.getCell(writeTemp-1));
//////                        System.out.println(writeContent);
//////                        writeCell.setCellValue(writeContent);
////                    }
////                }else{
////
////                }
//////                String newCarC1= stringObjectMap.get(writeSNoNewCarStr1).toString();String newCarC4= stringObjectMap.get(writeNewCarStr2).toString();
////            }
////            if("非新车".equals(isNewCarStr)){//
//////                String noNewCarC1= stringObjectMap.get("14").toString();String noNewCarC4= stringObjectMap.get("18").toString();
////            }
//
//
//
//
//
////            if(resultList.size() == 1){//说明已经找到值
////                Map<String, Object> stringObjectMap = resultList.get(0);
////                if("非新车".equals(isNewCarStr)){//
////                    String noNewCarC1= stringObjectMap.get("14").toString();String noNewCarC4= stringObjectMap.get("18").toString();
////                }
////                if("新车".equals(isNewCarStr)){//
////                    String newCarC1= stringObjectMap.get(writeSNoNewCarStr1).toString();String newCarC4= stringObjectMap.get(writeNewCarStr2).toString();
////                }
////            }
////            if(resultList.size()>1){//如果大于1 说明有多张情况包含
////                List<Map<String,Object>> resultList2 =  resultList.stream().filter((map)->{
////
////                }).collect(Collectors.toList());
////            }
//
//
////            log.info("-i{}----------size>{}",j,resultList.size());
//
//
//
////            int writeTemp = row.getLastCellNum();
////            if(row.getCell(writeTemp) == null){
////                Cell writeCell123 = row.createCell(writeTemp);
////                String writeContent = getCellValue(row.getCell(writeTemp-1));
////                System.out.println(writeContent);
////                writeCell123.setCellValue(writeContent);
////            }
//        }//获取行并进行循环
////        写入数据
//        FileOutputStream excelFileOutPutStream = new FileOutputStream("D:\\wwb\\lxj\\1.xlsx");
//        workbook.write(excelFileOutPutStream);
//        excelFileOutPutStream.flush();
//        excelFileOutPutStream.close();
//    }
////        获取单元格 内容
//    public String getCellValue(Cell cell){
//        String cellValue="";
//        if (cell == null ){return cellValue;}
//        CellType cellType = cell.getCellType();
//        //字符串
//        if (cellType == CellType.STRING) {
//            cellValue = cell.getStringCellValue().trim();
//            cellValue = (cellValue==null || "".equals(cellValue)) ? "" : cellValue;
//        }
//        //数据格式
//        if (cellType == CellType.NUMERIC) {
//            //判断日期类型
//            if (HSSFDateUtil.isCellDateFormatted(cell)) {
//                SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String date1 = dff.format(cell.getDateCellValue());
//                cellValue = date1;
//            } else {
//                //设置数据格式（"#.######"是几位小数）
//                cellValue = new DecimalFormat("#.##").format(cell.getNumericCellValue());
//            }
//        }
//        if (cellType == CellType.BOOLEAN) {
//            cellValue = String.valueOf(cell.getBooleanCellValue());
//        }
//        return cellValue;
//    }
////        获取 Workbook 内容
//    public Workbook getWorkbook(String filePath) throws IOException {
//        Workbook workbook = null;
//        final  String EXCEL_XLS = "xls";
//        final  String EXCEL_XLSX = "xlsx";
//        //判断文件
//        if (filePath != null && !"".equals(filePath)) {
//            File file = new File(filePath);
//            //判断格式
//            if (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)) {
//                //创建输入流对象
//                InputStream is = new FileInputStream(file);
//                //判断excel版本号
//                if (file.getName().endsWith(EXCEL_XLS)) {
//                    workbook = new HSSFWorkbook(is);
//                } else if (file.getName().endsWith(EXCEL_XLSX)) {
//                    workbook = new XSSFWorkbook(is);
//                }
//            } else {
//                System.out.println("文件不是excel");
//            }
//        } else {
//            System.out.println("文件不存在");
//        }
//        return workbook;
//    }
//
//
//    @Test
//    public void connJdbc() {
//        List<Map<String, Object>> mapsList = null;
//        try {
//            Map<String,String> paramMap = new HashMap<String,String>();
//            paramMap.put("w","w");
////            String sqlTemp = " select * from baseInfo where t2='经代渠道' and t3='机动车交通事故责任强制保险' and t4='家庭自用汽车' and t9='否' and t8='1是' ";
//            String sqlTemp = " select * from baseInfo ";
//            Map<String,String> paramsMapTemp = SqlParser.escape4select(paramMap);
//            mapsList = jdbcTemplate.queryForList(SqlParser.parse(sqlTemp, paramsMapTemp));
//            mapsList.stream().forEach(map -> {
//               map.forEach((key,value)->{
//                   System.out.println(key+"#"+value);
//               });
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
////            return mapsList;
//        }
////        return mapsList;
//    }
//
//    //在row最后一列写数据
//    public void writCellFunc(Row row, String c1Str, String c4Str){
//        log.info("{}----------->{}",c1Str,c4Str);
//        int writeTemp = row.getLastCellNum();
//        if(row.getCell(writeTemp) == null){
//            Cell writeCellC1 = row.createCell(writeTemp);
//            Cell writeCellC4 = row.createCell(writeTemp+1);
//            writeCellC1.setCellValue(c1Str);
//            writeCellC4.setCellValue(c4Str);
//        }
//    }
//
//
//
////    @Test
////    public void operateDataTest() throws Exception {
////        OperteExcel operateExcel = new OperteExcel();
////        operateExcel.operateExcel(operateExcel.connJdbc());
////
////    }
//
//
//}
//
//
//
