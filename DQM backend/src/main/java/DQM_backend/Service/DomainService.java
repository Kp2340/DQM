package DQM_backend.Service;

import DQM_backend.Model.Domain;
import DQM_backend.Repository.DomainRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DomainService {

    @Autowired
    private DomainRepository repositoryd;

    MultipartFile file;
    Workbook workbook;
    Sheet sheet;
    Row headerRow;
    List<String> columns;
    String filename;
    double[] min, max, maxx, minn, inconsistency;
    String[] type;
    List[] lists;
    int[][] selectedList;
    double average;
    int[] total,count,invalid;
    int j,c;


    //Upload Excel File
    public List<String> extractColumns(MultipartFile uploadedfile) throws IOException {
        file = uploadedfile;
        filename = uploadedfile.getOriginalFilename();
        workbook = WorkbookFactory.create(file.getInputStream());
        sheet = workbook.getSheetAt(0);
        headerRow = sheet.getRow(0);
        columns = new ArrayList<>();
        for (Cell cell : headerRow) {
            if(cell.getCellType()!=CellType.BLANK) columns.add(cell.getStringCellValue());
        }
        workbook.close();
        int x=columns.size();
        min=new double[x];
        minn=new double[x];
        max=new double[x];
        maxx=new double[x];
        inconsistency=new double[x];
        type=new String[x];
        total=new int[x];
        count=new int[x];
        invalid=new int[x];
        lists = new List[x];
        selectedList = new int[x][];
        return columns;
    }



    //Selected Rows for Domain Check
    public Object getRangeForDomain(String col, String type) throws IOException {
        j=columns.indexOf(col);
        int totalRows = sheet.getPhysicalNumberOfRows() - 1;
        min[j]=10000.0;
        max[j]=-10000.0;
        this.type[j]=type;
//        if(type=="") return "";
        count[j]=0;
        switch(type) {
            case "Integer":
                for (int i = 1; i <= totalRows; i++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() != CellType.NUMERIC || cell.getNumericCellValue()!=(int)cell.getNumericCellValue()) count[j]++;
                    else {
                        int x = (int) cell.getNumericCellValue();
                        if (x < min[j]) min[j] = x;
                        if (x > max[j]) max[j] = x;
                    }
                }
                break;
            case "Decimal":
                for (int i = 1; i <= totalRows; i++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() != CellType.NUMERIC || cell.getNumericCellValue()==(int)cell.getNumericCellValue()) count[j]++;
                    else {
                        double x = cell.getNumericCellValue();
                        if (x < min[j]) min[j] = x;
                        if (x > max[j]) max[j] = x;
                    }
                }
                break;
            case "List":
                Set<String> set=new HashSet<>();
                for (int i = 1; i <= totalRows; i++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() != CellType.STRING) count[j]++;
                    else set.add(cell.getStringCellValue());
                }
                List<String> list = new ArrayList<>(set);
                selectedList[j]=new int[list.size()];
                lists[j]=list;
                total[j]=totalRows;
                invalid[j]=0;
                inconsistency[j]=((int)(10000*count[j]/totalRows))/100.0;
                average = 0;
                int c=0;
                for(double i:inconsistency) {
                    if(i!=0) {
                        average += i;
                        c++;
                    }
                }
                average/=c;
                invalid[j]=count[j];
                list.add(totalRows+"");
                list.add(count[j]+"");
                list.add(inconsistency[j]+"");
                list.add(average+"");
                return list;
            case "String":
                for(int i = 1; i <= totalRows; i++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() != CellType.STRING) count[j]++;
                    else {
                        int len=cell.getStringCellValue().length();
                        if(len<min[j]) min[j]=len;
                        if(len>max[j]) max[j]=len;
                    }
                }
                break;
            case "":
                min[j]=0;
                max[j]=0;
                break;
        }
        total[j]=totalRows;
        minn[j]=min[j];
        maxx[j]=max[j];
        inconsistency[j]=((int)(10000*count[j]/totalRows))/100.0;
        invalid[j]=0;
        average = 0;
        c=0;
        for(double i:inconsistency) {
            if(i!=0) {
                average += i;
                c++;
            }
        }
        average /= c;
        return new double[]{min[j], max[j], totalRows, count[j], inconsistency[j], average};
    }

    //Calculate Domain
    public double[] calculateDomain(double min1, double max1, int j) throws Exception {
        String type=this.type[j];
        int totalRows = sheet.getPhysicalNumberOfRows() - 1;
        System.out.println(type);
        invalid[j]=0;
        if(min1<min[j] && max1>max[j]) return new double[]{invalid[j], inconsistency[j],  average};
        switch(type){
            case "Integer":
                for (int i = 1; i <= totalRows; i++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() != CellType.NUMERIC || cell.getNumericCellValue() != (int) cell.getNumericCellValue() || cell.getNumericCellValue()<(int)min1 || cell.getNumericCellValue()>(int)max1) invalid[j]++;
                }
                break;
            case "Decimal":
                for (int i = 1; i <= totalRows; i++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getNumericCellValue() == (int)cell.getNumericCellValue() || cell.getNumericCellValue()<min1 || cell.getNumericCellValue()>max1) invalid[j]++;
                }
                break;
            case "String":
                for(int i = 1; i <= totalRows; i++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() != CellType.STRING || cell.getStringCellValue().length()<min1 || cell.getStringCellValue().length()>max1) invalid[j]++;
                }
                break;
        }
        maxx[j]=max1;
        minn[j]=min1;
        inconsistency[j]= ((int) (10000 * invalid[j] /totalRows)) / 100.0;
        average = 0;
        c=0;
        for(double i:inconsistency) {
            if(i!=0) {
                average += i;
                c++;
            }
        }
        average /= c;
        System.out.println("i "+invalid[j]+", t "+totalRows+",i "+inconsistency[j]+",a "+average);

        return new double[]{invalid[j], inconsistency[j], average};
    }
    public double[] calculateDomainForList(int index, String value) {
        j=index;
        int valueIndex=lists[index].indexOf(value), totalRows = sheet.getPhysicalNumberOfRows() - 1;
        int add = selectedList[index][valueIndex] == 1 ? -1 : 1;
        selectedList[index][valueIndex] += add;
        for (int i = 1; i <= totalRows; i++) {
            Cell cell = sheet.getRow(i).getCell(index);
            if (cell != null && cell.getCellType() == CellType.STRING && value.equals(cell.getStringCellValue())) invalid[index]+=add;
        }
        inconsistency[index]=((int) (10000*invalid[index]/totalRows)) / 100.0;
        average = 0;
        c=0;
        for(double i:inconsistency) {
            if(i!=0) {
                average += i;
                c++;
            }
        }
        average /= c;
        System.out.println(",i "+invalid[index]+", t "+totalRows+",i "+inconsistency[index]+",a "+average);
        return new double[]{invalid[index], inconsistency[index], average};
    }

    //save Domain
    public void saveDomain(){
        String attributes="";
        String types="";
        String mins="";
        String maxs="";
        String inconsistencies="";
//        String averages=(double)((int)average)/100+"";
        for(int i=0; i<type.length; i++){
            if(type[i]!=null) {
                attributes+=columns.get(i)+",";
                types+=type[i]+",";
                mins+=minn[i]+",";
                maxs+=maxx[i]+",";
                inconsistencies+=inconsistency[i]+",";
            }
        }
        if(!attributes.isEmpty()) attributes=attributes.substring(0, attributes.length()-1);
        if(!types.isEmpty()) types=types.substring(0, types.length()-1);
        if(!mins.isEmpty()) mins=mins.substring(0, mins.length()-1);
        if(!maxs.isEmpty()) maxs=maxs.substring(0, maxs.length()-1);
        if(!inconsistencies.isEmpty()) inconsistencies=inconsistencies.substring(0, inconsistencies.length()-1);
        repositoryd.save(new Domain(filename, attributes, types, mins, maxs, inconsistencies, average+"", LocalDateTime.now()));
    }


    //Get All Domain
    public List<Domain> getAllDomains() {
        return repositoryd.findAll();
    }


    //Delete Domain By Id
    public void deleteDomainById(Long id){
        repositoryd.deleteById(id);
    }


    //Delete All Domain
    public void deleteDomainAll() {
        repositoryd.deleteAll();
    }
}
