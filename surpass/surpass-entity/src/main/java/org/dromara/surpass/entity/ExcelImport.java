/*
 * Copyright [2025] [JinBooks of copyright http://www.jinbooks.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */




package org.dromara.surpass.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * .
 *
 * @author Crystal.Sea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class ExcelImport {
	String id;
    @JsonIgnore
    protected MultipartFile excelFile;
    String updateExist;
    InputStream inputStream = null;
    Workbook workbook = null;

    @Column
    private String createdBy;

    @Column
    private Date createdDate;

    @Column
    private String modifiedBy;

    @Column
    private Date modifiedDate;


    public boolean isExcelNotEmpty() {
        return excelFile != null && !excelFile.isEmpty();
    }


    public Workbook biuldWorkbook() throws IOException {
        workbook = null;
        inputStream = excelFile.getInputStream();
        if (excelFile.getOriginalFilename().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (excelFile.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            throw new RuntimeException("Excel suffix error.");
        }
        return workbook;
    }

    public void closeWorkbook() {
//        if (inputStream != null) {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                log.error(e.getMessage(), e);
//            }
//        }
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
