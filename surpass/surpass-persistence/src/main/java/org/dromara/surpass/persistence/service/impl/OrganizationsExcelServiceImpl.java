/*
 * Copyright [2025] [Surpass of copyright http://www.surpass.com]
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






package org.dromara.surpass.persistence.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dromara.mybatis.jpa.service.impl.JpaServiceImpl;
import org.dromara.surpass.constants.ConstsHttpHeader;
import org.dromara.surpass.constants.ContentType;
import org.dromara.surpass.entity.ExcelImport;
import org.dromara.surpass.entity.idm.Organizations;
import org.dromara.surpass.entity.idm.UserInfo;
import org.dromara.surpass.persistence.mapper.OrganizationsMapper;
import org.dromara.surpass.persistence.service.OrganizationsExcelService;
import org.dromara.surpass.util.DateUtils;
import org.dromara.surpass.util.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import com.google.common.collect.Lists;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


@Repository
public class OrganizationsExcelServiceImpl extends JpaServiceImpl<OrganizationsMapper,Organizations> implements OrganizationsExcelService {
	static final Logger logger = LoggerFactory.getLogger(OrganizationsExcelServiceImpl.class);

	@Autowired
    OrganizationsMapper organizationsMapper;

	 /**
	     *       根据数据格式返回数据
     *
     * @param cell
     * @return
     */
    public static String getValue(Cell cell) {
        if (cell == null) {
            return "";
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.NUMERIC) {
            cell.setBlank();
            return String.valueOf(cell.getStringCellValue().trim());
        } else {
            return String.valueOf(cell.getStringCellValue().trim());
        }
    }

	private Organizations buildFromSheetRow(Row row, UserInfo currentUser) {
        Organizations organization = new Organizations();
        // 上级编码
        organization.setParentId(ExcelUtils.getValue(row, 0));
        // 上级名称
        organization.setParentName(ExcelUtils.getValue(row, 1));
        // 组织标识
        organization.setId(ExcelUtils.getValue(row, 2));
        // 组织标识
        organization.setOrgCode(ExcelUtils.getValue(row, 3));
        // 组织名称
        organization.setOrgName(ExcelUtils.getValue(row, 4));
        // 组织全称
        organization.setFullName(ExcelUtils.getValue(row, 5));
        // 名称路径
        organization.setNamePath(ExcelUtils.getValue(row, 6));
        // 组织类型
        organization.setType(ExcelUtils.getValue(row, 7));
        // 所属分支机构
        organization.setDivision(ExcelUtils.getValue(row, 8));
        // 级别
        String level = ExcelUtils.getValue(row, 9);
        organization.setLevel(level.equals("") ? 1 : Integer.parseInt(level));
        // 排序
        String sortIndex = ExcelUtils.getValue(row, 10);
        organization.setSortIndex(sortIndex.equals("") ? 1 : Integer.parseInt(sortIndex));

        // 状态
        String status = ExcelUtils.getValue(row, 11);
        organization.setStatus(status.equals("启用") ? 1 : 2);

        // 联系人
        organization.setContact(ExcelUtils.getValue(row, 12));
        // 联系电话
        organization.setPhone(ExcelUtils.getValue(row, 13));
        // 邮箱
        organization.setEmail(ExcelUtils.getValue(row, 14));
        // 传真
        organization.setFax(ExcelUtils.getValue(row, 15));
        // 工作-国家
        organization.setCountry(ExcelUtils.getValue(row, 16));
        // 工作-省
        organization.setRegion(ExcelUtils.getValue(row, 17));
        // 工作-城市
        organization.setLocality(ExcelUtils.getValue(row, 18));
        // 工作-地址
        organization.setAddress(ExcelUtils.getValue(row, 19));
        // 邮编
        organization.setPostalCode(ExcelUtils.getValue(row, 20));
        // 详细描述
        organization.setDescription(ExcelUtils.getValue(row, 21));
        return organization;
    }

	public void importFromExcel(ExcelImport excelImportFile, UserInfo currentUser) {
		try {
			List<Organizations> orgsList = Lists.newArrayList();
			Workbook workbook = excelImportFile.biuldWorkbook();
			int sheetSize = workbook.getNumberOfSheets();
			//遍历sheet页
			for (int i = 0; i < sheetSize; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				int rowSize = sheet.getLastRowNum() + 1;
				for (int j = 1; j < rowSize; j++) {
                    //遍历行
					Row row = sheet.getRow(j);
					if (row == null || j < 2) {
                        //略过空行和前2行
					} else {//其他行是数据行
						orgsList.add(buildFromSheetRow(row, currentUser));
					}
				}
			}
			// 数据去重
            if (!CollectionUtils.isEmpty(orgsList)) {
                orgsList = orgsList.stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Organizations::getId))),
                                ArrayList::new));

                for (Organizations org : orgsList) {
                    Organizations exist = super.get(org.getId());
                    if (exist == null) {
                        organizationsMapper.insert(org);
                    } else {
                        organizationsMapper.update(org);
                    }
                }
            }
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
          excelImportFile.closeWorkbook();
		}
	}


	public void exportToExcel(String exportType,Organizations organization, HttpServletResponse response) {
		List<Organizations> allOrg = null;
		//判断导出模板还是导出组织机构 org 还是template
        if (StringUtils.isNotEmpty(exportType) && "org".equalsIgnoreCase(exportType)) {
            allOrg = organizationsMapper.queryOrgs(organization);
        }
        Workbook workbook = null;
        try {
            if (allOrg != null && allOrg.size() > 5000) {
                //数据量大导出方案，超过5000条数据采用对象，
                // 比如设置最大内存量为5000行， new SXSSFWookbook(5000)，
                // 当行数达到 5000 时，把内存持久化写到文件中，以此逐步写入，避免OOM。解决了大数据下导出的问题
                workbook = new SXSSFWorkbook(5000);
            } else {
                workbook = new XSSFWorkbook();
            }
            //创建sheet
            Sheet sheet = workbook.createSheet("组织机构");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Maxkey Org Export " + DateUtils.getCurrentDateTimeAsString());
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 22));
            //构建头
            row = sheet.createRow(1);
            row.createCell(0).setCellValue("上级标识");
            /*row.createCell(1).setCellValue("上级编码");*/
            row.createCell(1).setCellValue("上级名称");
            row.createCell(2).setCellValue("组织标识");
            row.createCell(3).setCellValue("组织编码");
            row.createCell(4).setCellValue("组织名称");
            row.createCell(5).setCellValue("组织全称");
            /*row.createCell(7).setCellValue("编码路径");*/
            row.createCell(6).setCellValue("名称路径");
            row.createCell(7).setCellValue("组织类型");
            row.createCell(8).setCellValue("所属分支机构");
            row.createCell(9).setCellValue("级别");
            row.createCell(10).setCellValue("排序");
            row.createCell(11).setCellValue("状态");
            row.createCell(12).setCellValue("联系人");
            row.createCell(13).setCellValue("联系电话");
            row.createCell(14).setCellValue("邮箱");
            row.createCell(15).setCellValue("传真");
            row.createCell(16).setCellValue("工作-国家");
            row.createCell(17).setCellValue("工作-省");
            row.createCell(18).setCellValue("工作-城市");
            row.createCell(19).setCellValue("工作-地址");
            row.createCell(20).setCellValue("邮编");
            row.createCell(21).setCellValue("详细描述");
            //导入数据=================================
            if (allOrg != null) {
                for (int i = 0; i < allOrg.size(); i++) {
                    Organizations org = allOrg.get(i);
                    row = sheet.createRow(i + 2);
                    row.createCell(0).setCellValue(org.getParentId());
                    /*row.createCell(1).setCellValue(org.getParentCode());*/
                    row.createCell(1).setCellValue(org.getParentName());
                    row.createCell(2).setCellValue(org.getId());
                    row.createCell(3).setCellValue(org.getOrgCode());
                    row.createCell(4).setCellValue(org.getOrgName());
                    row.createCell(5).setCellValue(org.getFullName());

                    /*row.createCell(7).setCellValue(org.getCodePath());*/
                    row.createCell(6).setCellValue(org.getNamePath());
                    row.createCell(7).setCellValue(org.getType());
                    row.createCell(8).setCellValue(org.getDivision());
                    row.createCell(9).setCellValue(org.getLevel());
                    row.createCell(10).setCellValue(org.getSortIndex());
                    row.createCell(11).setCellValue(org.getStatus() == 1 ? "启用" : "禁用");
                    row.createCell(12).setCellValue(org.getContact());
                    row.createCell(13).setCellValue(org.getPhone());
                    row.createCell(14).setCellValue(org.getEmail());
                    row.createCell(15).setCellValue(org.getFax());
                    row.createCell(16).setCellValue(org.getCountry());
                    row.createCell(17).setCellValue(org.getRegion());
                    row.createCell(18).setCellValue(org.getLocality());
                    row.createCell(19).setCellValue(org.getAddress());
                    row.createCell(20).setCellValue(org.getPostalCode());
                    row.createCell(21).setCellValue(org.getDescription());
                }
            }
            String fileName = "org";
            fileName = URLEncoder.encode(fileName, "UTF8");
            response.setContentType(ContentType.APPLICATION_MS_EXCEL);
            response.setHeader(ConstsHttpHeader.CONTENT_DISPOSITION, ConstsHttpHeader.ATTACHMENT_FILE.formatted(fileName));
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.error("error:", e);
        } finally {
            if (allOrg != null) {
                allOrg.clear();
            }
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    logger.error("error close ", e);
                }
            }
        }
	}

}
