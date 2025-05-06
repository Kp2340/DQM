package DQM_backend.Service;

import DQM_backend.Model.Format;
import DQM_backend.Repository.FormatRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class FormatService {
    @Autowired
    private FormatRepository repository;
    MultipartFile file;
    Workbook workbook;
    Sheet sheet;
    Row headerRow;
    String filename, formatCheck, attributes;
    Double errorRate;
    List<String> columns=new ArrayList<>();
    String data;

    @Autowired
    private FormatRepository repositoryf;

    private static final List<String> test = new ArrayList<>(Arrays.asList("NOT PROVIDED", "NOT AVAILABLE", "NULL", "N/A", "N.A.","NA", "", null));
    private static final List<String> dateFormats = Arrays.asList("dd-MM-yyyy", "dd-MMM", "dd/MM/yyyy", "dd/MM/yy", "dd/M/yy",
            "yyyy/MM/dd", "yyyy/dd/MM", "dd-MMMMM-yyyy", "dd-MM-yy", "d-M-yy", "d.M.yy", "dd MMMMM yyyy",
            "dd-MMM-yy", "dd-MM-yyyy HH:mm", "MMM-yy", "MM-dd-yyyy", "MM.dd.yyyy", "MMM-dd-yyyy",
            "yyyy-MM-dd HH:mm", "MM-dd-yy hh:mm", "yyyy-MM-dd H:mm", "yyyy-MM-dd", "yyyy.MM.dd");
    private static final String perfectFormat = "yyyy-MM-dd";
    private static final DateTimeFormatter perfectFormatter = DateTimeFormatter.ofPattern(perfectFormat);
    private static final List<List<String>> stationCodes = new ArrayList<>(Arrays.asList(
            Arrays.asList(" "),
            Arrays.asList(" "),
            Arrays.asList("AH", "AD", "AF", "AK", "AN", "AY", "AZ", "BD", "BE", "BH", "CH", "CD", "MS", "HX", "DR", "DD", "DG", "EE", "ED", "FD", "GK", "GD", "GY", "GR", "HD", "HW", "ET", "JP", "JM", "JL", "JU", "JT", "CJ", "LD", "MJ", "MV", "MB", "ND", "SC", "SV", "ST", "NS", "NK", "PC", "FL", "PP", "RC", "RN", "RJ", "RU", "RE", "RK", "SA", "SL", "TJ", "TN", "UD", "ON", "BL", "VM", "VG", "WL", "WR", "AB", "AE", "AG", "AI", "AJ", "AL", "AO", "AP", "AS", "AT", "BA", "BB", "BC", "BG", "BJ", "BK", "BN", "BP", "BR", "BT", "BU", "BV", "BY", "CC", "CE", "CI", "CT", "CU", "CW", "DB", "DE", "DI", "DJ", "DK", "DL", "DM", "DN", "DO", "DS", "DY", "EL", "EM", "EN", "FA", "FK", "FM", "GA", "GB", "GE", "GG", "GH", "GJ", "GL", "GM", "GP", "GW", "HG", "HK", "HM", "HN", "HP", "IB", "JA", "JN", "JO", "KD", "KH", "KJ", "KK", "KL", "KN", "KO", "KP", "KS", "KT", "KY", "LC", "LM", "LP", "LR", "LS", "LW", "MA", "MC", "ME", "MG", "MH", "MK", "ML", "MM", "MO", "MW", "MX", "MY", "NB", "NG", "NH", "NI", "NM", "NN", "NR", "NU", "NW", "OM", "OV", "PB", "PG", "PK", "PL", "PM", "PN", "PO", "PS", "PT", "PU", "PW", "RA", "RG", "RM", "RR", "RV", "SD", "SE", "SI", "SM", "SN", "SR", "SS", "SW", "SY", "TA", "TK", "TP", "TR", "TT", "TU", "UA", "UJ", "UM", "UR", "VD", "VI", "VL", "VN", "VP", "VR", "VS", "VT", "VU", "VV", "WC", "YA", "YG", "YL", "YP", "YT", "ZB", "ZN", "ZW"),
            Arrays.asList("ABR", "AGC", "ANG", "ADI", "AII", "ALD", "LWR", "AWR", "AWY", "UMB", "ASR", "AKP", "ATP", "ANV", "APR", "ARA", "AJJ", "ASK", "ASN", "ARJ", "AWB", "AMH", "BPB", "BUG", "BGK", "BPM", "BKP", "BTC", "BLS", "BPQ", "BUI", "BNQ", "BDC", "BKI", "SBC", "BNC", "BWT", "BQK", "BPP", "BBK", "BJU", "BTU", "BBL", "BWN", "BNY", "BME", "BOF", "BOE", "BST", "BTI", "BXN", "BAP", "BGM", "BAY", "BTH", "BZU", "BOY", "BHC", "BGP", "BTE", "BYT", "BTT", "BVC", "BHL", "BIX", "BNW", "BPR", "BPL", "BBS", "BSL", "BJP", "BJO", "BKN", "BSP", "BNV", "RRB", "VBL", "BHP", "BVI", "BTD", "BMP", "BEM", "BAU", "BUW", "BXR", "CNO", "CAA", "CKP", "CSN", "CLD", "CPH", "CDG", "CNI", "CRP", "CPK", "(T)", "CGL", "MAS", "CPR", "CDM", "JRU", "CHI", "CLX", "CRJ", "COR", "CTO", "CPU", "CAR", "CUR", "CBE", "ONR", "CTC", "DRD", "DHD", "DLO", "DTO", "DMO", "DNR", "DBG", "DVG", "DDN", "DOS", "DLI", "DEC", "DEE", "DSA", "DVL", "DMN", "DHN", "DAB", "DPJ", "DMM", "DWR", "DAS", "DHO", "DLJ", "DNC", "DHG", "DUI", "DLN", "DMV", "DGG", "DKJ", "DGR", "DWK", "ERS", "ERN", "ETW", "FDB", "FKD", "FTD", "FTP", "FUT", "FKA", "FZD", "FZP", "FZR", "FBG", "FKG", "GDG", "GJL", "GIM", "GGC", "GHD", "GTS", "GZB", "GDA", "GMO", "GKP", "GDV", "GDR", "GTL", "GNT", "GUV", "GHY", "GWL", "GYN", "HBJ", "HJP", "HLZ", "HDB", "HOJ", "HMH", "HPU", "HRR", "HPP", "HSA", "HTE", "NZM", "HIL", "HMT", "HUP", "HNL", "HSR", "HBD", "HPT", "HBG", "HWH", "UBL", "HYB", "IGP", "IAA",
                    "IPR", "JBP", "JSM", "JHL", "JRC", "JUC", "JPG", "JMP", "JAT", "JAM", "JNH", "JNU", "JYG", "JLR", "JAJ", "JHS", "JGM", "JSG", "JBN", "JTJ", "JND", "KCG", "COA", "KCF", "KLK", "KLL", "KYN", "KYQ", "KQL", "CAN", "CNB", "CPA", "CPJ", "KUN", "KRR", "KGQ", "KSJ", "KGM", "KIR", "KTE", "KPD", "KZJ", "KGG", "KLD", "KMT", "KNW", "KGP", "KEX", "KUR", "KNE", "KSG", "KQN", "KPG", "KTW", "KKP", "CLT", "KNJ", "KJM", "KMU", "KWV", "LMP", "LRJ", "LGH", "LGL", "LAR", "LKU", "LUR", "LHU", "LTT", "LNL", "LFG", "LDH", "LMG", "MTM", "MDT", "MAD", "MAO", "MBI", "MDP", "MDU", "MSH", "MBA", "MLN", "MKC", "MNM", "MUV", "MAQ", "MKP", "MUR", "MMR", "MNE", "MXN", "MTJ", "MAU", "MTC", "MTD", "MTP", "MDN", "MRJ", "MZP", "MKA", "MKI", "MUE", "MGS", "BCT", "MZS", "MZR", "MOZ", "MFP", "MYS", "NSL", "NAB", "NAD", "NCJ", "NCR", "NGP", "NIR", "SBP", "SLI", "STR", "STA", "SAP", "SWM", "SWV", "SCT", "SDB", "SHG", "SXK", "SPP", "SHM", "SGZ", "SKB", "SML", "SRR", "SCL", "SSA", "SBZ", "SMI", "SPC", "SCC", "SOD", "SOL", "SUR", "SEE", "CHE", "SLN", "SOG", "NBD", "NLD", "NRE", "NED", "NGN", "NDB", "NDL", "NKE", "NRW", "NUH", "NLR", "NOQ", "NBQ", "NCB", "NFK", "NJP", "NMZ", "NDD", "NDO", "NMH", "NZB", "NLP", "OGL", "PNU", "PSA", "PGT", "PLK", "PNP", "PBN", "PTK", "PTA", "PNC", "PFM", "PBE", "PPI", "PTJ", "PBR", "PBH", "PDY", "PLO", "PAU", "PRR", "QLN", "RBL", "RIG", "RJY", "RJK", "RKM", "RJQ", "RGD", "RJT", "RJN", "RPJ", "RDM", "RMD", "RMM",
                    "RMR", "RMU", "RPH", "RHA", "RNC", "RNY", "ROJ", "RTM", "RXL", "RGS", "ROK", "ROU", "SRF", "SGL", "SRE", "SHC", "SBG", "SRU", "SLO", "SPJ", "TDD", "TBM", "TLY", "TEL", "TSI", "TVR", "TPJ", "TEN", "TIR", "TIG", "TCR", "TVC", "TMR", "TDL", "UDZ", "UHP", "UDN", "UJN", "UNA", "UCR", "BDJ", "BRC", "MEJ", "BSB", "VAK", "BSR", "VSG", "VRL", "BHS", "BZA", "VPT", "VZM", "VRI", "WKR", "YPR", "ZBD", "AAG", "AAK", "AAL", "AAM", "AAR", "AAS", "AAY", "ABB", "ABD", "ABH", "ABI", "ABP", "ABS", "ABZ", "ACH", "ACK", "ACL", "ACN", "ADB", "ADD", "ADE", "ADH", "ADR", "ADT", "AED", "AEL", "AEP", "AFK", "AFR", "AGA", "AGB", "AGD", "AGI", "AGN", "AGR", "AGY", "AGZ", "AHA", "AHH", "AHN", "AHO", "AIG", "AIR", "AIT", "AJE", "AJH", "AJI", "AJL", "AJN", "AJP", "AJR", "AKD", "AKE", "AKJ", "AKN", "AKR", "AKT", "AKU", "AKV", "AKW", "ALB", "ALK", "ALM", "ALN", "ALU", "ALW", "ALY", "AMB", "AMC", "AME", "AMG", "AMI", "AML", "AMM", "AMP", "AMQ", "AMS", "AMT", "AMW", "AMX", "AMY", "ANA", "ANB", "AND", "ANH", "ANI", "ANK", "ANO", "ANP", "ANR", "APA", "APD", "APH", "APK", "APL", "APQ", "APT", "AQG", "ARD", "ARE", "ARK", "ARN", "ARP", "ARQ", "ARR", "ARV", "ARW", "ASD", "ASH", "ASL", "ASO", "AST", "ASV", "ATA", "ATB", "ATE", "ATH", "ATL", "ATQ", "ATR", "ATS", "ATT", "AUB", "AUR", "AUS", "AVD", "AVK", "AVL", "AVS", "AWL", "AWP", "AWS", "AXR", "AYD", "AYM", "AYN", "AYV", "AZP", "AZR", "BAB", "BAE", "BAH", "BAL", "BAM", "BAO", "BAQ", "BAR",
                    "BAT", "BAV", "BAZ", "BBA", "BBM", "BBO", "BBR", "BBU", "BBW", "BCA", "BCD", "BCH", "BCK", "BCL", "BCN", "BCO", "BCP", "BCQ", "BCU", "BCY", "BDB", "BDE", "BDH", "BDI", "BDL", "BDM", "BDN", "BDU", "BDW", "BDY", "BDZ", "BEA", "BEB", "BEF", "BEG", "BEH", "BEK", "BEO", "BEP", "BEQ", "BER", "BET", "BEW", "BEY", "BFD", "BFJ", "BFM", "BFT", "BFY", "BGA", "BGG", "BGH", "BGN", "BGQ", "BGS", "BGU", "BGW", "BGX", "BGZ", "BHD", "BHG", "BHI", "BHT", "BHU", "BHV", "BHW", "BHY", "BHZ", "BIA", "BIC", "BID", "BIG", "BIH", "BIJ", "BIM", "BIO", "BIP", "BIQ", "BIR", "BIU", "BIY", "BJA", "BJD", "BJE", "BJF", "BJI", "BJK", "BJM", "BJN", "BJQ", "BJW", "BKA", "BKC", "BKD", "BKH", "BKJ", "BKL", "BKO", "BKU", "BLC", "BLD", "BLG", "BLK", "BLL", "BLM", "BLO", "BLP", "BLT", "BLU", "BLW", "BLX", "BLY", "BLZ", "BMB", "BMC", "BMD", "BMF", "BMH", "BMI", "BMK", "BMN", "BMO", "BMQ", "BMR", "BMT", "BMU", "BMW", "BNE", "BNG", "BNH", "BNI", "BNL", "BNM", "BNN", "BNO", "BNP", "BNR", "BNS", "BNT", "BNU", "BNZ", "BOD", "BOG", "BOI", "BOJ", "BON", "BOR", "BOV", "BOW", "BOZ", "BPA", "BPC", "BPD", "BPF", "BPH", "BPK", "BPO", "BPS", "BPY", "BPZ", "BQA", "BQG", "BQI", "BQM", "BQN", "BQP", "BQQ", "BQR", "BQU", "BRA", "BRB", "BRD", "BRE", "BRG", "BRH", "BRK", "BRM", "BRN", "BRR", "BRS", "BRU", "BRY", "BRZ", "BSC", "BSE", "BSJ", "BSN", "BSQ", "BSX", "BSY", "BSZ", "BTA", "BTG", "BTJ", "BTK", "BTL", "BTO", "BTP", "BTR", "BTS", "BTV", "BTW", "BTX",
                    "BTY", "BUA", "BUB", "BUD", "BUH", "BUL", "BUM", "BUP", "BUQ", "BUS", "BUT", "BUX", "BVA", "BVB", "BVH", "BVL", "BVM", "BVN", "BVP", "BVQ", "BVS", "BVU", "BVV", "BWA", "BWB", "BWC", "BWD", "BWF", "BWH", "BWI", "BWK", "BWL", "BWM", "BWP", "BWR", "BWS", "BWW", "BWY", "BXA", "BXF", "BXJ", "BXM", "BXP", "BXQ", "BXY", "BYC", "BYD", "BYL", "BYN", "BYP", "BYR", "BYY", "BZK", "BZM", "BZN", "BZO", "BZR", "BZY", "CAF", "CAG", "CAI", "CAJ", "CAP", "CBF", "CBG", "CBH", "CBJ", "CBK", "CBM", "CBN", "CBP", "CBT", "CBU", "CBY", "CCA", "CCG", "CCH", "CCI", "CCT", "CDA", "CDC", "CDH", "CDL", "CDP", "CDQ", "CDS", "CEL", "CEU", "CGE", "CGH", "CGI", "CGN", "CGO", "CGR", "CGY", "CHA", "CHB", "CHD", "CHH", "CHJ", "CHL", "CHM", "CHN", "CHP", "CHV", "CIL", "CIT", "CJL", "CJM", "CJN", "CJR", "CJS", "CJW", "CKB", "CKD", "CKE", "CKI", "CKK", "CKR", "CKS", "CKU", "CKX", "CLA", "CLC", "CLF", "CLG", "CLI", "CLJ", "CLN", "CLO", "CLR", "CLU", "CMA", "CMC", "CMJ", "CMU", "CMW", "CMX", "CMY", "CMZ", "CNA", "CNC", "CND", "CNE", "CNF", "CNH", "CNK", "CNL", "CNR", "CNS", "CNT", "CNY", "COB", "COD", "COE", "COM", "COO", "COT", "CPB", "CPD", "CPE", "CPL", "CPN", "CPP", "CPS", "CPT", "CRC", "CRL", "CRN", "CRR", "CRW", "CRY", "CSA", "CSL", "CSM", "CSZ", "CTA", "CTE", "CTF", "CTH", "CTL", "CTQ", "CTR", "CTS", "CTT", "CTY", "CUD", "CUK", "CUL", "CUX", "CVB", "CVJ", "CVP", "CVR", "CWA", "CWI", "CYN", "CYR", "DAA", "DAD", "DAJ", "DAL", "DAM",
                    "DAN", "DAO", "DAP", "DAR", "DAU", "DAV", "DAZ", "DBA", "DBB", "DBD", "DBI", "DBL", "DBM", "DBO", "DBP", "DBR", "DBU", "DBV", "DBW", "DBY", "DCK", "DCU", "DDA", "DDC", "DDE", "DDJ", "DDK", "DDL", "DDM", "DDP", "DDR", "DDS", "DDW", "DDY", "DEB", "DED", "DEG", "DEL", "DEP", "DER", "DES", "DEV", "DEW", "DFR", "DGA", "DGI", "DGJ", "DGN", "DGQ", "DGS", "DGT", "DGU", "DHA", "DHI", "DHJ", "DHM", "DHP", "DHR", "DHS", "DHU", "DHW", "DIA", "DIB", "DIC", "DIG", "DIH", "DIL", "DIQ", "DIR", "DIT", "DIW", "DJG", "DJI", "DJJ", "DKD", "DKO", "DKS", "DKT", "DKW", "DKX", "DKZ", "DLB", "DLC", "DLD", "DLK", "DLP", "DLQ", "DLR", "DLW", "DMA", "DMC", "DME", "DMG", "DMH", "DMK", "DMP", "DMR", "DMT", "DMW", "DNA", "DND", "DNE", "DNK", "DNL", "DNM", "DNN", "DNT", "DNV", "DNW", "DNZ", "DOA", "DOB", "DOD", "DOE", "DOG", "DOH", "DOK", "DOL", "DOR", "DOZ", "DPA", "DPC", "DPD", "DPF", "DPH", "DPL", "DPP", "DPR", "DPU", "DPX", "DPZ", "DQG", "DQL", "DQN", "DRB", "DRH", "DRL", "DRO", "DRS", "DRU", "DRW", "DRZ", "DSB", "DSD", "DSJ", "DSK", "DSL", "DSM", "DSO", "DSS", "DTC", "DTJ", "DTL", "DTR", "DTV", "DUA", "DUB", "DUD", "DUJ", "DUN", "DUR", "DUS", "DUT", "DVA", "DVD", "DVR", "DVY", "DWA", "DWD", "DWG", "DWI", "DWJ", "DWO", "DWP", "DWV", "DWX", "DWZ", "DXD", "DXG", "DXH", "DXK", "DXN", "DXR", "DYD", "DYK", "DYP", "DZA", "DZB", "EDD", "EDN", "EKC", "EKM", "EKN", "ELM", "ELP", "ENB", "EPR", "ERC", "ERL", "ETM", "EVA", "FAN", "FBD",
                    "FDK", "FGR", "FKB", "FKK", "FKM", "FLD", "FLK", "FPS", "FRD", "FSP", "FTG", "FTS", "FYZ", "FZL", "GAA", "GAD", "GAE", "GAG", "GAJ", "GAM", "GAP", "GAR", "GAV", "GAW", "GBA", "GBB", "GBD", "GBE", "GBP", "GCH", "GCT", "GDB", "GDD", "GDE", "GDI", "GDL", "GDM", "GDP", "GDX", "GDZ", "GEA", "GED", "GEG", "GER", "GGA", "GGB", "GGD", "GGJ", "GGM", "GGN", "GGO", "GGS", "GGT", "GHG", "GHH", "GHJ", "GHQ", "GHR", "GHX", "GID", "GII", "GIN", "GIO", "GIR", "GIZ", "GJD", "GJJ", "GKB", "GKC", "GKD", "GKH", "GKK", "GKM", "GKX", "GKY", "GLA", "GLG", "GLH", "GLP", "GLU", "GLY", "GMD", "GMH", "GMM", "GMR", "GMS", "GMU", "GMX", "GNA", "GNC", "GND", "GNG", "GNH", "GNJ", "GNO", "GNP", "GNR", "GNS", "GNU", "GNW", "GOC", "GOD", "GOH", "GOI", "GOK", "GOL", "GOM", "GON", "GOP", "GOV", "GOY", "GOZ", "GPB", "GPD", "GPF", "GPH", "GPI", "GPJ", "GPR", "GPU", "GPX", "GPZ", "GQL", "GRA", "GRB", "GRD", "GRF", "GRG", "GRH", "GRI", "GRL", "GRM", "GRO", "GRX", "GRY", "GSD", "GSI", "GSO", "GSP", "GSW", "GSX", "GTA", "GTE", "GTF", "GTI", "GTK", "GTM", "GTR", "GTT", "GTU", "GTW", "GTX", "GUB", "GUD", "GUH", "GUM", "GUR", "GUX", "GVB", "GVD", "GVG", "GVI", "GVL", "GVN", "GVR", "GWA", "GWD", "GWM", "GWS", "GWV", "GYM", "GZH", "GZL", "GZM", "GZN", "HAA", "HAD", "HAN", "HAR", "HAS", "HAT", "HAY", "HBW", "HCP", "HCR", "HDA", "HDD", "HDK", "HDL", "HDN", "HDP", "HDS", "HDU", "HDW", "HEM", "HER", "HFG", "HFZ", "HGH", "HGI", "HGJ", "HGR", "HGT",
                    "HGY", "HIR", "HJI", "HJL", "HJO", "HKG", "HKH", "HKL", "HKP", "HKR", "HLG", "HLK", "HLN", "HLR", "HMG", "HMK", "HML", "HMO", "HMP", "HMR", "HMY", "HNA", "HNK", "HNS", "HOL", "HPO", "HPR", "HRB", "HRD", "HRG", "HRH", "HRI", "HRM", "HRN", "HRS", "HRT", "HRV", "HRW", "HSD", "HSI", "HSK", "HSL", "HSQ", "HSX", "HTC", "HTD", "HTJ", "HTK", "HTL", "HTT", "HTZ", "HUK", "HVD", "HVM", "HVR", "HYG", "HYL", "HYT", "HZD", "HZR", "IBL", "ICL", "IDG", "IDH", "IDJ", "IDL", "IDP", "IDR", "IHP", "IJK", "IKK", "IKR", "ILA", "INJ", "INP", "IPL", "IPM", "IQB", "IQG", "IRP", "ISA", "ISH", "ISM", "ITA", "ITE", "ITR", "IZN", "JAA", "JAB", "JAC", "JAL", "JAN", "JAO", "JAQ", "JAW", "JBB", "JBG", "JBK", "JBL", "JBX", "JCH", "JCL", "JCN", "JDB", "JDH", "JDI", "JDL", "JDN", "JDR", "JDW", "JEN", "JEP", "JER", "JES", "JGD", "JGE", "JGF", "JGI", "JGJ", "JGN", "JGR", "JGW", "JHA", "JHD", "JHG", "JHN", "JHW", "JIA", "JID", "JIL", "JIR", "JIT", "JJG", "JJK", "JJN", "JJP", "JJR", "JKA", "JKE", "JKH", "JKM", "JKN", "JKS", "JLD", "JLL", "JLN", "JLS", "JLT", "JLW", "JLY", "JMD", "JMG", "JMK", "JMQ", "JMS", "JMT", "JMU", "JMV", "JNA", "JNE", "JNL", "JNM", "JNO", "JNR", "JNZ", "JOA", "JOB", "JOC", "JOM", "JON", "JOP", "JOQ", "JOR", "JOS", "JPD", "JPE", "JPH", "JPI", "JPL", "JPM", "JPZ", "JRA", "JRI", "JRJ", "JRK", "JRO", "JRQ", "JRS", "JRT", "JRX", "JSA", "JSH", "JSI", "JSP", "JSR", "JSV", "JTB", "JTG", "JTI", "JTL", "JTN", "JTO", "JTP",
                    "JTR", "JTU", "JTV", "JTW", "JTX", "JUD", "JUK", "JUL", "JUP", "JVA", "JVL", "JVN", "JWB", "JWL", "JWO", "JWP", "JYK", "JYM", "JYP", "KAD", "KAG", "KAH", "KAL", "KAN", "KAQ", "KAR", "KAT", "KAV", "KBA", "KBE", "KBH", "KBI", "KBJ", "KBK", "KBL", "KBM", "KBN", "KBQ", "KBR", "KBY", "KCA", "KCC", "KCD", "KCH", "KCI", "KCJ", "KCM", "KCN", "KCP", "KDE", "KDF", "KDG", "KDI", "KDL", "KDM", "KDN", "KDP", "KDQ", "KDT", "KDU", "KDZ", "KEA", "KEB", "KED", "KEF", "KEG", "KEH", "KEI", "KEJ", "KEK", "KEM", "KEN", "KER", "KFA", "KFD", "KFF", "KFI", "KFP", "KFT", "KFU", "KGA", "KGB", "KGD", "KGE", "KGF", "KGI", "KGL", "KGN", "KGS", "KGT", "KGW", "KGX", "KGZ", "KHC", "KHD", "KHE", "KHH", "KHJ", "KHM", "KHN", "KHQ", "KHR", "KHS", "KHT", "KHU", "KID", "KIK", "KIM", "KIN", "KIP", "KIS", "KIT", "KIV", "KJA", "KJG", "KJH", "KJI", "KJJ", "KJN", "KJS", "KJT", "KJU", "KJV", "KJW", "KJY", "KJZ", "KKB", "KKD", "KKG", "KKI", "KKJ", "KKK", "KKM", "KKN", "KKV", "KKW", "KKZ", "KLA", "KLB", "KLG", "KLH", "KLJ", "KLP", "KLQ", "KLT", "KLU", "KLV", "KLX", "KLZ", "KMC", "KMD", "KMI", "KMJ", "KMK", "KML", "KMM", "KMN", "KMQ", "KMS", "KMV", "KMX", "KMY", "KND", "KNN", "KNO", "KNP", "KNT", "KOA", "KOF", "KOI", "KOJ", "KOL", "KOM", "KOO", "KOP", "KOQ", "KOU", "KOV", "KPA", "KPB", "KPE", "KPI", "KPK", "KPL", "KPM", "KPN", "KPP", "KPQ", "KPT", "KPV", "KPY", "KPZ", "KQA", "KQE", "KQK", "KQR", "KQT", "KQU", "KQW", "KQZ", "KRA", "KRC", "KRD",
                    "KRE", "KRG", "KRH", "KRJ", "KRL", "KRP", "KRQ", "KRS", "KRU", "KRV", "KRW", "KRY", "KSB", "KSC", "KSD", "KSE", "KSF", "KSH", "KSI", "KSK", "KSM", "KSN", "KSP", "KSU", "KSV", "KSW", "KSX", "KTA", "KTD", "KTF", "KTH", "KTI", "KTJ", "KTM", "KTO", "KTQ", "KTR", "KTU", "KTV", "KTX", "KTY", "KUA", "KUD", "KUE", "KUF", "KUH", "KUK", "KUM", "KUV", "KUW", "KUX", "KVA", "KVC", "KVG", "KVJ", "KVK", "KVL", "KVM", "KVR", "KVS", "KVU", "KVX", "KVZ", "KWB", "KWF", "KWH", "KWI", "KWJ", "KWM", "KWN", "KWR", "KXA", "KXG", "KXH", "KXJ", "KXN", "KXO", "KXP", "KXT", "KXX", "KYE", "KYG", "KYI", "KYJ", "KYM", "KYP", "KYS", "KYT", "KYX", "KZA", "KZB", "KZE", "KZK", "KZQ", "KZT", "KZU", "KZY", "LAE", "LAK", "LAL", "LAT", "LAU", "LAV", "LBA", "LBN", "LCH", "LCN", "LDA", "LDE", "LDM", "LDP", "LDR", "LDU", "LDW", "LDX", "LDY", "LGB", "LGO", "LGT", "LHA", "LHB", "LHD", "LHN", "LHW", "LIG", "LJN", "LJR", "LKA", "LKD", "LKE", "LKN", "LKO", "LKQ", "LKR", "LKS", "LKZ", "LLH", "LLI", "LLJ", "LLR", "LLU", "LMC", "LMD", "LMK", "LMN", "LMO", "LMT", "LMY", "LNA", "LNH", "LNK", "LNN", "LNO", "LNQ", "LNR", "LNV", "LOA", "LOV", "LPG", "LPH", "LPI", "LPJ", "LPR", "LRA", "LRD", "LRU", "LSD", "LSG", "LSI", "LSR", "LSX", "LTA", "LTR", "LTV", "LXR", "MAA", "MAE", "MAG", "MAI", "MAL", "MAM", "MAN", "MAP", "MAR", "MAV", "MAY", "MAZ", "MBB", "MBD", "MBF", "MBG", "MBM", "MBS", "MBT", "MBW", "MBY", "MCA", "MCI", "MCJ", "MCL", "MCN", "MCO", "MCQ",
                    "MCS", "MCT", "MCU", "MCV", "MDA", "MDB", "MDD", "MDE", "MDF", "MDH", "MDJ", "MDL", "MDR", "MDS", "MDW", "MEC", "MED", "MEL", "MEM", "MEP", "MEQ", "MES", "MET", "MEX", "MEZ", "MFB", "MFC", "MFJ", "MFL", "MFM", "MFQ", "MFR", "MGB", "MGD", "MGF", "MGG", "MGI", "MGM", "MGN", "MGO", "MGR", "MGW", "MHD", "MHH", "MHJ", "MHL", "MHN", "MHO", "MHP", "MHQ", "MHU", "MHV", "MID", "MIH", "MIK", "MIL", "MIN", "MIQ", "MIU", "MJA", "MJF", "MJG", "MJL", "MJN", "MJO", "MJP", "MJS", "MJY", "MJZ", "MKB", "MKD", "MKJ", "MKL", "MKM", "MKN", "MKO", "MKR", "MKS", "MKT", "MKU", "MKX", "MLA", "MLB", "MLC", "MLD", "MLG", "MLH", "MLI", "MLJ", "MLK", "MLM", "MLO", "MLS", "MLU", "MLV", "MLW", "MLX", "MLY", "MLZ", "MMA", "MMB", "MMC", "MMD", "MMH", "MMK", "MML", "MMM", "MMP", "MMS", "MMV", "MMY", "MMZ", "MNC", "MNF", "MNG", "MNI", "MNJ", "MNP", "MNQ", "MNU", "MNV", "MNX", "MNY", "MOB", "MOF", "MOI", "MOL", "MOM", "MON", "MOO", "MOP", "MOR", "MOT", "MOU", "MOW", "MOY", "MPA", "MPF", "MPH", "MPL", "MPR", "MPT", "MPU", "MQE", "MQH", "MQL", "MQO", "MQQ", "MQR", "MQS", "MQU", "MQX", "MQZ", "MRA", "MRB", "MRE", "MRF", "MRH", "MRK", "MRL", "MRN", "MRR", "MRT", "MRU", "MRV", "MSD", "MSK", "MSO", "MSP", "MSQ", "MSR", "MST", "MSW", "MSZ", "MTB", "MTE", "MTH", "MTL", "MTR", "MTT", "MTU", "MTY", "MUA", "MUD", "MUG", "MUK", "MUP", "MUT", "MUW", "MUY", "MUZ", "MVE", "MVF", "MVG", "MVI", "MVJ", "MVL", "MVN", "MVO", "MVR", "MVW", "MWD", "MWH",
                    "MWK", "MWM", "MWR", "MWT", "MWX", "MXA", "MXH", "MXK", "MXL", "MXO", "MXR", "MXT", "MXW", "MXY", "MYA", "MYG", "MYJ", "MYK", "MYL", "MYM", "MYN", "MYP", "MYR", "MYU", "MYX", "MYY", "MZC", "MZH", "MZL", "MZM", "MZN", "MZZ", "NAC", "NAG", "NAK", "NAM", "NAN", "NAR", "NAS", "NAT", "NAW", "NBG", "NBH", "NBM", "NBP", "NBR", "NBX", "NCA", "NDE", "NDH", "NDJ", "NDM", "NDN", "NDR", "NDT", "NDU", "NDZ", "NEM", "NEP", "NEW", "NGA", "NGD", "NGF", "NGG", "NGI", "NGK", "NGM", "NGO", "NGR", "NGT", "NGW", "NHH", "NHK", "NHM", "NHN", "NHR", "NHT", "NHY", "NIA", "NID", "NIG", "NIL", "NIM", "NIP", "NIQ", "NIU", "NIV", "NJM", "NJT", "NKB", "NKD", "NKI", "NKK", "NKM", "NKP", "NKR", "NLC", "NLE", "NLI", "NLK", "NLV", "NLY", "NMA", "NMD", "NMJ", "NMK", "NML", "NMM", "NMX", "NNA", "NNE", "NNL", "NNM", "NNN", "NNO", "NNP", "NNR", "NNW", "NOA", "NOG", "NOI", "NOK", "NOL", "NOY", "NPD", "NPI", "NPL", "NPM", "NPR", "NPS", "NPT", "NPW", "NQR", "NRA", "NRD", "NRG", "NRI", "NRK", "NRL", "NRM", "NRN", "NRO", "NRP", "NRR", "NRS", "NRT", "NRV", "NRX", "NSD", "NSP", "NSU", "NTN", "NTV", "NTW", "NTZ", "NUA", "NUD", "NUJ", "NUQ", "NUR", "NVG", "NVL", "NVS", "NVT", "NVU", "NWA", "NWB", "NWD", "NWH", "NWP", "NWR", "NWU", "NXN", "NYH", "NYK", "NYN", "NYP", "NYY", "NZD", "NZT", "OBR", "OCH", "OCR", "ODC", "ODG", "ODM", "OEA", "OKA", "OLA", "OPL", "ORC", "ORH", "ORR", "OSN", "OTP", "OTR", "OYR", "PAA", "PAD", "PAE", "PAI", "PAK", "PAM",
                    "PAN", "PAO", "PAP", "PAR", "PAS", "PAV", "PAW", "PAX", "PAY", "PAZ", "PBA", "PBD", "PBL", "PBM", "PBP", "PBQ", "PBS", "PBV", "PBW", "PCC", "PCH", "PCL", "PCN", "PCQ", "PCR", "PCT", "PCV", "PCX", "PCZ", "PDA", "PDD", "PDE", "PDG", "PDH", "PDL", "PDM", "PDP", "PDQ", "PDR", "PDT", "PDU", "PDV", "PDZ", "PED", "PEH", "PEI", "PEM", "PEN", "PEP", "PER", "PES", "PFL", "PFR", "PFU", "PGA", "PGG", "PGI", "PGK", "PGN", "PGR", "PGU", "PGW", "PGZ", "PHA", "PHD", "PHK", "PHN", "PHQ", "PHR", "PHS", "PHV", "PIA", "PIC", "PIH", "PIJ", "PIL", "PIP", "PIT", "PIZ", "PJA", "PJH", "PJK", "PJN", "PKD", "PKE", "PKL", "PKO", "PKP", "PKQ", "PKR", "PKT", "PKU", "PKW", "PLA", "PLC", "PLD", "PLE", "PLG", "PLI", "PLJ", "PLL", "PLM", "PLP", "PLS", "PLU", "PLW", "PLY", "PMH", "PMK", "PML", "PMN", "PMO", "PMP", "PMR", "PMS", "PMY", "PND", "PNE", "PNF", "PNI", "PNJ", "PNK", "PNM", "PNO", "PNQ", "PNT", "PNW", "POF", "POK", "POM", "PON", "POO", "POR", "POU", "POY", "POZ", "PPA", "PPC", "PPD", "PPF", "PPG", "PPH", "PPJ", "PPM", "PPN", "PPO", "PPR", "PPT", "PPU", "PPZ", "PQD", "PQE", "PQL", "PQM", "PQN", "PQY", "PRB", "PRE", "PRF", "PRG", "PRH", "PRI", "PRJ", "PRK", "PRL", "PRM", "PRN", "PRP", "PRT", "PRU", "PSB", "PSD", "PSO", "PSR", "PST", "PTB", "PTC", "PTD", "PTE", "PTH", "PTM", "PTN", "PTP", "PTS", "PTT", "PTU", "PTZ", "PUA", "PUD", "PUE", "PUK", "PUL", "PUN", "PUO", "PUS", "PUT", "PUU", "PUV", "PUX", "PVD", "PVI", "PVL", "PVP",
                    "PVR", "PVU", "PWL", "PWS", "PYG", "PYM", "PYX", "QDN", "QLD", "QLM", "QRP", "QRS", "QSR", "QTR", "RAA", "RAI", "RAJ", "RAL", "RAM", "RAU", "RAY", "RBA", "RBD", "RBG", "RBK", "RBN", "RBR", "RBS", "RBZ", "RCG", "RCP", "RDD", "RDF", "RDG", "RDL", "RDP", "RDT", "REG", "REI", "REJ", "REN", "RES", "REW", "RFJ", "RGB", "RGI", "RGJ", "RGL", "RGM", "RGO", "RGP", "RGQ", "RGU", "RHE", "RHG", "RHI", "RHN", "RHR", "RID", "RJA", "RJB", "RJG", "RJI", "RJL", "RJO", "RJP", "RJR", "RJS", "RJU", "RKB", "RKD", "RKH", "RKI", "RKL", "RKO", "RKS", "RKY", "RLA", "RLG", "RLO", "RLR", "RMA", "RMC", "RMF", "RMH", "RMO", "RMP", "RMT", "RMV", "RMX", "RNA", "RNE", "RNG", "RNH", "RNI", "RNJ", "RNL", "RNN", "RNO", "RNQ", "RNR", "RNT", "RNV", "RNW", "ROA", "ROB", "ROP", "ROS", "RPD", "RPI", "RPR", "RPT", "RPZ", "RQJ", "RRE", "RRI", "RRJ", "RRL", "RSA", "RSG", "RSH", "RSM", "RSR", "RTA", "RTG", "RTI", "RTK", "RTP", "RUB", "RUG", "RUI", "RUJ", "RUL", "RUM", "RUR", "RUT", "RVD", "RVK", "RWH", "RWJ", "RWL", "RWO", "RXN", "RXW", "RYP", "RYS", "RZN", "SAA", "SAB", "SAD", "SAE", "SAG", "SAH", "SAI", "SAL", "SAN", "SAO", "SAR", "SAS", "SAT", "SAU", "SAV", "SAW", "SBB", "SBD", "SBE", "SBH", "SBI", "SBL", "SBO", "SBR", "SBS", "SBT", "SBV", "SBW", "SBY", "SCH", "SCI", "SCM", "SCO", "SCP", "SCQ", "SDD", "SDE", "SDF", "SDG", "SDH", "SDL", "SDM", "SDN", "SDS", "SDT", "SDV", "SEB", "SEC", "SED", "SEG", "SEH", "SEI", "SEM", "SEN", "SEO", "SES",
                    "SET", "SEU", "SEV", "SEY", "SFC", "SFG", "SFK", "SFM", "SFW", "SFX", "SFY", "SGD", "SGE", "SGF", "SGG", "SGJ", "SGM", "SGO", "SGP", "SGR", "SGS", "SGV", "SGW", "SHE", "SHF", "SHH", "SHK", "SHR", "SHU", "SHV", "SHZ", "SIC", "SID", "SIE", "SIF", "SII", "SIL", "SIM", "SIO", "SIP", "SIQ", "SIR", "SIW", "SJF", "SJL", "SJN", "SJP", "SJQ", "SJS", "SJT", "SKA", "SKF", "SKI", "SKJ", "SKK", "SKM", "SKN", "SKP", "SKQ", "SKR", "SKS", "SKT", "SKY", "SLB", "SLD", "SLF", "SLG", "SLH", "SLJ", "SLM", "SLR", "SLS", "SLT", "SLW", "SLY", "SMC", "SME", "SMK", "SMO", "SMP", "SMR", "SMT", "SMU", "SMZ", "SNC", "SNE", "SNF", "SNH", "SNI", "SNK", "SNL", "SNM", "SNN", "SNP", "SNQ", "SNS", "SNT", "SNU", "SNX", "SOA", "SOB", "SOC", "SOE", "SOH", "SOJ", "SOM", "SOP", "SOR", "SOS", "SOT", "SOW", "SOY", "SPD", "SPE", "SPF", "SPK", "SPL", "SPN", "SPO", "SPR", "SPT", "SPZ", "SQD", "SQL", "SQN", "SQR", "SRA", "SRC", "SRI", "SRJ", "SRK", "SRL", "SRN", "SRO", "SRP", "SRT", "SRW", "SRX", "SSB", "SSL", "SSM", "SSN", "SSR", "SSW", "STB", "STC", "STD", "STF", "STL", "STN", "STP", "STW", "SUC", "SUD", "SUL", "SUP", "SUW", "SUX", "SUZ", "SVA", "SVB", "SVD", "SVH", "SVI", "SVJ", "SVL", "SVM", "SVN", "SVO", "SVT", "SVV", "SVW", "SVX", "SVZ", "SWA", "SWC", "SWD", "SWE", "SWF", "SWI", "SWJ", "SWO", "SWR", "SWS", "SWX", "SXB", "SXN", "SXP", "SXT", "SXV", "SXW", "SYA", "SYC", "SYI", "SYJ", "SYK", "SYL", "SYM", "SYN", "SYU", "SYW", "SZA", "SZB",
                    "SZK", "SZM", "SZR", "SZY", "TAE", "TAN", "TAO", "TAR", "TAT", "TAV", "TAY", "TAZ", "TBA", "TBB", "TBH", "TBN", "TBT", "TBV", "TCH", "TCN", "TDH", "TDN", "TDO", "TDP", "TDR", "TDU", "TDV", "TEA", "TGA", "TGH", "TGL", "TGM", "TGN", "TGP", "TGQ", "TGT", "TGU", "THB", "THE", "THJ", "THM", "THO", "THP", "THV", "THY", "TIA", "TIL", "TIM", "TIP", "TIS", "TIT", "TIU", "TIW", "TJA", "TJD", "TJP", "TKA", "TKB", "TKD", "TKE", "TKG", "TKI", "TKJ", "TKN", "TKQ", "TKR", "TKU", "TLC", "TLD", "TLE", "TLH", "TLI", "TLJ", "TLR", "TLU", "TLZ", "TMC", "TMD", "TME", "TML", "TMQ", "TMV", "TMX", "TMZ", "TNA", "TNI", "TNK", "TNL", "TNM", "TNP", "TNR", "TNX", "TOD", "TOI", "TOK", "TOM", "TOU", "TPC", "TPE", "TPF", "TPG", "TPH", "TPQ", "TPT", "TPU", "TPW", "TPY", "TQA", "TQB", "TQM", "TQN", "TRA", "TRB", "TRG", "TRK", "TRL", "TRM", "TRO", "TRR", "TRT", "TSA", "TSD", "TSF", "TSK", "TSL", "TSR", "TSS", "TTB", "TTH", "TTI", "TTL", "TTO", "TTP", "TTR", "TTU", "TTZ", "TUA", "TUL", "TUN", "TUP", "TUR", "TUX", "TVD", "TVF", "TVG", "TVI", "TVL", "TVN", "TVP", "TWB", "TWG", "TWL", "TXD", "TZD", "UAA", "UAM", "UAR", "UBC", "UBN", "UBR", "UCA", "UCB", "UCH", "UCP", "UDL", "UDM", "UDS", "UDT", "UDX", "UGD", "UGR", "UHL", "UHR", "UIH", "UJA", "UJH", "UJP", "UKA", "UKC", "UKE", "UKH", "UKL", "UKR", "ULA", "ULB", "ULD", "ULG", "ULL", "ULT", "UMH", "UMN", "UMR", "UMS", "UND", "UNI", "UNK", "UNL", "UPA", "UPD", "UPI", "UPL", "UPR", "UPW",
                    "URD", "URG", "URI", "URK", "URL", "URN", "USD", "USL", "UTA", "UTD", "UTL", "UTR", "UVD", "VAA", "VAE", "VAL", "VAT", "VBR", "VBW", "VCN", "VDA", "VDD", "VDE", "VDG", "VDH", "VDI", "VDK", "VDL", "VDN", "VDS", "VDV", "VDY", "VEN", "VGI", "VGL", "VGN", "VGT", "VID", "VJA", "VJD", "VJF", "VJM", "VJR", "VKA", "VKB", "VKI", "VKM", "VKN", "VKR", "VKT", "VLA", "VLD", "VLI", "VLP", "VLR", "VLU", "VLY", "VMA", "VMD", "VMU", "VNA", "VNB", "VND", "VNE", "VNG", "VNK", "VON", "VPG", "VPL", "VPO", "VPR", "VPY", "VPZ", "VRA", "VRD", "VRE", "VRG", "VRH", "VRK", "VRM", "VRQ", "VRR", "VRU", "VRV", "VRX", "VSI", "VST", "VSU", "VSV", "VSW", "VTA", "VTJ", "VTL", "VTM", "VTN", "VTV", "VVA", "VVB", "VVD", "VVE", "VVF", "VVL", "VVM", "VVN", "VVV", "VWA", "VXD", "VXM", "VYA", "VYK", "VYN", "WAB", "WAT", "WDM", "WDR", "WDS", "WEL", "WFD", "WGA", "WHM", "WJR", "WKA", "WKI", "WND", "WPR", "WRC", "WRE", "WRR", "WRS", "WSB", "WSC", "WSE", "WSJ", "WTJ", "WTR", "YAL", "YDK", "YFP", "YLG", "YLK", "YLM", "YNK", "YPD", "YTL", "ZNA", "ZPI", "ZPL", "ZRD", "ZZZ"),
            Arrays.asList("ADRA", "ALJN", "APDJ", "ALLP", "AMLA", "ANND", "ANDN", "ANGL", "BDME", "BNDA", "BDTS", "BNKI", "BRGA", "BRKA", "BRWD", "BEAS", "BTJL", "BVRM", "BVRT", "BHUJ", "BINA", "BTTR", "BKSC", "CHKB", "CSMT", "CNGR", "CKTD", "CUPJ", "NDLS", "DEOS", "DBRT", "DNRP", "DURG", "GADJ", "GAYA", "GLPT", "GOGH", "GUNA", "HAPA", "HSRA", "INDB", "JJKR", "JSME", "JIND", "KNKD", "CAPE", "KKDI", "KAWR", "KWAE", "KIUL", "KCVL", "KRPU", "KRBA", "KOTA", "KTYM", "KUDA", "KRNT", "KKDE", "LEDO", "LUNI", "MBNR", "MLDT", "MHOW", "MRGA", "CSTM", "MURI", "NDKD", "AMTA", "SRNT", "SDAH", "SEGM", "SKTN", "SMQL", "SMET", "SVPI", "SMBJ", "SOJN", "SIKR", "SGUJ", "SLGR", "SGRL", "SKZR", "SURI", "SGNR", "SSPN", "NLDA", "NLDM", "NDLS", "NTSK", "NOLI", "OKHA", "ORAI", "PLNI", "PGTN", "PNME", "PNVL", "PNBE", "PDKT", "PUNE", "PURI", "PRNA", "RPAN", "RTGH", "RGDA", "REWA", "SDLP", "SIOB", "SUNR", "TLHR", "TATA", "TZTB", "TPTY", "TUNI", "VSKP", "WADI", "ABFC", "ABKA", "ABKP", "ABLE", "ACLE", "ACND", "ADTL", "ADVI", "AGAS", "AGTL", "AHLR", "AJIT", "AJNI", "AKOR", "AKOT", "AKRD", "AKVD", "AKVX", "ALAI", "ALER", "ALNI", "AMBR", "AMIN", "AMLI", "AMNR", "AMPA", "AMRO", "AMSA", "ANAS", "ANKL", "ANMD", "ANPR", "ANSB", "ANTU", "ANVR", "APTA", "ARCL", "ARGD", "ARVI", "ASAF", "ASKN", "ASLU", "ATMO", "ATRU", "ATUL", "AUBR", "AUWA", "AVLI", "AVRD", "BADR", "BAGA", "BAJN", "BAKA", "BAKL", "BALE", "BALR", "BALU", "BAMA", "BANI", "BANO", "BARH", "BARL",
                    "BATL", "BAWA", "BBAI", "BBDE", "BBGN", "BBMN", "BBPM", "BBTR", "BCHL", "BCOB", "BCRD", "BDCR", "BDDR", "BDHA", "BDHL", "BDHN", "BDHY", "BDMJ", "BDNK", "BDNP", "BDRL", "BDVT", "BDWA", "BDWD", "BDWL", "BDXX", "BEHR", "BEHS", "BERO", "BGAE", "BGBR", "BGHU", "BGKT", "BGMR", "BGPR", "BGRA", "BGSF", "BGTA", "BGTN", "BGVN", "BGWD", "BHET", "BHJA", "BHKD", "BHLA", "BHLK", "BHLP", "BHME", "BHNE", "BHNS", "BHTA", "BHTK", "BHTL", "BHTN", "BHTR", "BHUA", "BHWA", "BIDR", "BIJR", "BILD", "BILK", "BIML", "BIPR", "BIRD", "BISH", "BIWK", "BJMD", "BJMR", "BJNR", "BJPL", "BJRI", "BKHR", "BKLE", "BKNG", "BKRD", "BKRO", "BKTH", "BLAX", "BLDI", "BLDK", "BLGR", "BLGT", "BLLI", "BLMK", "BLMR", "BLND", "BLNI", "BLOR", "BLPR", "BLPU", "BLRD", "BLRE", "BLSA", "BLSD", "BLSN", "BLTR", "BLWL", "BMCK", "BMGA", "BMGM", "BMHR", "BMKJ", "BMLL", "BMPL", "BMPR", "BMSN", "BMSR", "BNAR", "BNCE", "BNDM", "BNGN", "BNHL", "BNLW", "BNPP", "BNSA", "BNTL", "BNUD", "BNVD", "BNWC", "BNXR", "BOBS", "BOKE", "BONA", "BORA", "BOTI", "BPHB", "BPKA", "BPRD", "BPRS", "BPZA", "BRAG", "BRDH", "BRGM", "BRGT", "BRGW", "BRJN", "BRLA", "BRLY", "BRMD", "BRMO", "BRMP", "BRMT", "BRND", "BRPL", "BRPT", "BRRG", "BRTK", "BRVR", "BRYA", "BSAE", "BSBR", "BSDA", "BSDL", "BSDP", "BSGN", "BSKR", "BSLE", "BSPD", "BSPN", "BSPR", "BSQP", "BSRL", "BSRX", "BSWD", "BTKD", "BTKL", "BTKP", "BTPD", "BTPR", "BTRA", "BUBR", "BUDI", "BUDM", "BUPH", "BURN", "BVNR", "BVRX", "BWRA",
                    "BXLL", "BYHA", "BYNR", "BYPL", "BZJT", "BZLE", "CAMU", "CASA", "CBEE", "CBSA", "CDGR", "CDLD", "CDMR", "CDRL", "CDSL", "CHBR", "CHBT", "CHCR", "CHII", "CHKE", "CHLK", "CHNN", "CHRA", "CHRM", "CHTI", "CHTS", "CHTX", "CKDL", "CKNI", "CKOD", "CLAT", "CLDY", "CLKA", "CLVR", "CMDP", "CMNR", "CNDM", "CNPR", "COAX", "CPDR", "CPLE", "CRKR", "CROA", "CRWA", "CSDR", "CTHR", "CTKT", "CTND", "CTRD", "CTYL", "DABN", "DAKE", "DAPD", "DARA", "DARI", "DAVM", "DBEC", "DBHL", "DBKA", "DBLA", "DDCE", "DEHR", "DELO", "DEMU", "DEOR", "DGDG", "DGHA", "DGLE", "DGPP", "DHKR", "DHMZ", "DHND", "DHNL", "DHPR", "DHRJ", "DHRR", "DHVR", "DING", "DINR", "DIPA", "DISA", "DIVA", "DJKR", "DKAE", "DKBJ", "DKDE", "DKJR", "DKLU", "DKNT", "DKPM", "DKWA", "DLGN", "DLNA", "DLPR", "DMBR", "DMLE", "DMPR", "DNDI", "DNEA", "DNHK", "DNKL", "DNRA", "DNRE", "DOHM", "DPLN", "DRGJ", "DRLA", "DRSN", "DRTP", "DRWN", "DSLP", "DSNI", "DTAE", "DUBH", "DURE", "DUSI", "DVGM", "DWNA", "DZKT", "ETAH", "ETUE", "FGSB", "GAGA", "GAJB", "GALA", "GALE", "GAMI", "GANG", "GANL", "GDHA", "GDPL", "GDPT", "GDVX", "GDYA", "GGAR", "GHGL", "GHLE", "GHNA", "GHNH", "GHUM", "GIMB", "GJMB", "GJUT", "GLGT", "GMAN", "GMDA", "GMDN", "GMIA", "GNGD", "GNGT", "GNNA", "GOLE", "GOPA", "GOPG", "GOTN", "GOVR", "GPAE", "GPPR", "GRBL", "GRHM", "GRMR", "GRRG", "GRRU", "GRWD", "GSPR", "GTJT", "GTKD", "GTLM", "GTNR", "GULR", "GUMA", "GVMR", "HJLI", "HLAR", "HLDD", "HLDR", "HLKT", "HMPR",
                    "HNDR", "HRNR", "HRPG", "HRSN", "HTGR", "IDAR", "INDM", "IPPM", "JACN", "JADR", "JAIS", "JALD", "JARI", "JDNA", "JDNX", "JEUR", "JHAR", "JHMR", "JKPR", "JMKL", "JMKR", "JMKT", "JMPT", "JNDC", "JNKR", "JNTR", "JOBA", "JRLE", "JRMG", "JSKA", "JTRD", "JTTN", "JUDW", "KADI", "KALD", "KALE", "KALN", "KAMG", "KANJ", "KANO", "KANS", "KAPG", "KART", "KASR", "KASU", "KATA", "KATL", "KATR", "KAWT", "KBPR", "KBRV", "KCKI", "KCNR", "KDBM", "KDCR", "KDHA", "KDLG", "KDLP", "KDLR", "KDMR", "KDNL", "KDPR", "KDTR", "KEMA", "KEPR", "KESR", "KFPR", "KGLE", "KGMR", "KGRA", "KHAT", "KHBJ", "KHCN", "KHED", "KHKN", "KHMA", "KHNM", "KHPI", "KHPL", "KHRJ", "KHRK", "KHRS", "KHTG", "KHTU", "KHXB", "KIKA", "KILE", "KITA", "KJME", "KKAH", "KKGM", "KKLR", "KKLU", "KKLX", "KKNA", "KKPM", "KKPR", "KKRD", "KKRM", "KLAR", "KLBA", "KLGD", "KLMG", "KLNK", "KLNP", "KLOD", "KLPM", "KLRE", "KLTR", "KLYN", "KMAE", "KMAH", "KMBK", "KMBL", "KMDR", "KMLI", "KMLR", "KMME", "KMNC", "KMND", "KMNR", "KMRJ", "KMSD", "KMST", "KMTI", "KNDI", "KNDP", "KNGN", "KNHN", "KNLS", "KNNA", "KNNK", "KNPL", "KNPR", "KNRG", "KNRT", "KNSR", "KNVT", "KODI", "KOHR", "KOKA", "KOLR", "KONY", "KOTI", "KPDH", "KPKD", "KPLE", "KPLL", "KPNA", "KPRD", "KPRR", "KPTN", "KRAI", "KRAR", "KRBO", "KRCD", "KRDH", "KRDL", "KRIH", "KRJA", "KRJD", "KRKP", "KRLI", "KRLR", "KRMD", "KRMI", "KRND", "KRNR", "KRPR", "KRPT", "KRSA", "KRSL", "KRTH", "KRXA", "KSNG", "KSNR", "KSPR", "KSRA",
                    "KSTH", "KSVM", "KSWR", "KTGA", "KTGD", "KTHA", "KTHU", "KTKH", "KTKL", "KTKR", "KTLA", "KTMA", "KTOA", "KTPR", "KTRD", "KTRH", "KTRR", "KTSH", "KTYR", "KUDL", "KULE", "KUMB", "KUPR", "KURJ", "KUTL", "KVDU", "KVLS", "KVNJ", "KWGN", "KYOP", "KZTW", "LAUL", "LCAE", "LGDH", "LHLL", "LKBL", "LKNA", "LKPE", "LLGM", "LMNR", "LMTD", "LOHA", "LONI", "LTHR", "LTRR", "LUSA", "MABA", "MABD", "MABG", "MADP", "MADR", "MAHE", "MAHO", "MAKM", "MALB", "MALK", "MALM", "MALX", "MANI", "MANK", "MARD", "MAUR", "MBCT", "MBGA", "MBNL", "MBSK", "MBVT", "MCLA", "MCLE", "MCPE", "MCVM", "MDBP", "MDDP", "MDGR", "MDHP", "MDKD", "MDKU", "MDLE", "MDLM", "MDNR", "MDPB", "MDPR", "MDRW", "MDVL", "MDVR", "MELH", "MFKA", "MGLE", "MGLP", "MGME", "MGRL", "MHAD", "MHDP", "MHND", "MHRG", "MINJ", "MIRA", "MITA", "MJBT", "MJRI", "MKDN", "MKHR", "MKPR", "MKPT", "MKRA", "MKRD", "MKRN", "MKSR", "MLGH", "MLGT", "MLHA", "MLMR", "MLPR", "MLSU", "MLTR", "MMDA", "MMLN", "MNAE", "MNDH", "MNDR", "MNGD", "MNSR", "MNUR", "MOGA", "MOMU", "MOTC", "MOTH", "MPLM", "MPLR", "MRBL", "MRDD", "MRDL", "MRDW", "MRIJ", "MRND", "MRPL", "MRPR", "MRTL", "MRWS", "MSDN", "MSDR", "MSMD", "MSMI", "MSOD", "MSSD", "MTDM", "MTHH", "MTHP", "MTIA", "MTNC", "MTNL", "MTPC", "MTPR", "MUGA", "MUGR", "MULK", "MUUA", "MVLK", "MYGL", "MZGI", "NANA", "NASP", "NAZJ", "NBHM", "NBUJ", "NBVJ", "NDAE", "NDAZ", "NDPR", "NDPU", "NERI", "NGJN", "NGLL", "NGLT", "NGRT", "NGTG", "NGTN", "NILD",
                    "NILE", "NIRA", "NKDO", "NLKR", "NLPD", "NNKR", "NOMD", "NPNR", "NRDP", "NRGR", "NRKR", "NRLR", "NROD", "NRPA", "NRPD", "NRWI", "NRZB", "NTWL", "NVLN", "NVRD", "OBVP", "OSRA", "PALI", "PALM", "PANP", "PASA", "PATA", "PAVP", "PBKS", "PCLI", "PCLM", "PDGM", "PDGN", "PDPL", "PDRD", "PERN", "PGRL", "PHRH", "PKNS", "PKRA", "PKRD", "PLJE", "PLMA", "PLMX", "PLPM", "PLSN", "PLVA", "PNDM", "PNGM", "PNPL", "PNYA", "PPGT", "PPLI", "PPRH", "PRBZ", "PRCA", "PRDL", "PRKD", "PRKE", "PRKH", "PRLI", "PRNC", "PRNG", "PRPT", "PRTL", "PRTP", "PRWD", "PSDA", "PTBY", "PTKP", "PTLI", "PTPU", "PTRD", "PTRE", "PTRL", "PTRU", "PVPT", "PVRD", "PYOL", "QMRS", "RAGM", "RAIR", "RAJP", "RAJR", "RANI", "RBGJ", "RCGT", "RDDE", "RDHP", "RDRA", "RDUM", "RECH", "RGPM", "RHNE", "RIGA", "RJPB", "RJPM", "RKSH", "RKSN", "RMGJ", "RMGM", "RMNP", "RNBD", "RNBT", "RNIS", "RNJD", "RNPR", "RNRD", "ROHA", "RORA", "ROZA", "RPAR", "RPRD", "RPUR", "RRME", "RSNA", "RSNR", "RSWT", "RSYI", "RTGN", "RUPC", "RUPR", "RURA", "RVKH", "RWTB", "SAGR", "SAHI", "SALE", "SALR", "SAMT", "SANR", "SASG", "SASR", "SBHN", "SBHR", "SBLJ", "SBLT", "SBNR", "SBPD", "SBRA", "SCKR", "SCTN", "SDGH", "SDGM", "SDLK", "SDMD", "SDMK", "SDPN", "SELU", "SGAM", "SGDM", "SGKM", "SGLA", "SGND", "SGRA", "SGRD", "SGRE", "SGUT", "SHDM", "SHDR", "SHLT", "SHMI", "SHNG", "SHNR", "SHNX", "SHRD", "SHTA", "SHTT", "SIHO", "SILO", "SINI", "SIRN", "SJNP", "SJSM", "SJTR", "SKGH", "SKLR", "SKPA",
                    "SKPT", "SKVL", "SLGH", "SLHP", "SLKN", "SLKR", "SLKX", "SLNA", "SLPM", "SLPP", "SLRD", "SLRP", "SMAE", "SMBL", "SMBX", "SMCP", "SMLA", "SMLG", "SMPR", "SMRR", "SMTA", "SNBD", "SNDD", "SNGN", "SNGP", "SNGR", "SNKL", "SNKR", "SNLR", "SNPR", "SNRR", "SNSL", "SNSN", "SNSR", "SNTD", "SNVR", "SOAE", "SOGR", "SONI", "SONR", "SORO", "SPLE", "SPRD", "SPTR", "SRAS", "SRBR", "SRDR", "SRGH", "SRGM", "SRGT", "SRHA", "SRID", "SRJM", "SRKI", "SRKN", "SRNR", "SRPJ", "SRPR", "SRSI", "SRTL", "SRTN", "SRUR", "SRVA", "SRVX", "SRWN", "SSIA", "SSKA", "SSPD", "STDR", "STJT", "STKT", "STNL", "STPM", "STPT", "STRC", "STSN", "STUR", "SUDV", "SUJH", "SUKP", "SULH", "SUMR", "SUPR", "SURC", "SURL", "SURM", "SURP", "SVGA", "SVGL", "SVHE", "SVJR", "SVKD", "SVKS", "SVNR", "SVPM", "SVPR", "SVRP", "SWMM", "SWNI", "SWPR", "SWRT", "SYWN", "TAKL", "TAKU", "TAPA", "TBAE", "TDPR", "TDRS", "TELI", "TELO", "TENI", "TETA", "THAN", "THDR", "THKU", "THMR", "THUR", "THVM", "TIBI", "TIHU", "TISI", "TKBN", "TKHE", "TKKD", "TKRI", "TKYR", "TLGP", "TLKH", "TLMD", "TLNH", "TLWA", "TNGL", "TNKU", "TNRU", "TORI", "TPND", "TPTN", "TRAH", "TRAN", "TRDI", "TRKR", "TRSR", "TRTR", "TRVL", "TUNG", "TUVR", "TUWA", "UDGR", "UGWE", "ULNR", "UMED", "UMNR", "UMRA", "UMRI", "UNDI", "UNLA", "UREN", "URMA", "URPR", "USLP", "VAPI", "VAPM", "VARD", "VDGN", "VEER", "VELI", "VJPJ", "VKNR", "VLDR", "VLYN", "VNRD", "VNUP", "VRVL", "WAIR", "WDLN", "WENA", "XXXX")));
    private static final List<List<String>> districts = new ArrayList<>(Arrays.asList(
        Arrays.asList(" "),
        Arrays.asList(" "),
        Arrays.asList(" "),
        Arrays.asList("DIU", "UNA", "BID", "MON", "MAU"),
        Arrays.asList("GAYA", "DURG", "TAPI", "JIND", "DODA", "DHAR", "GUNA", "REWA", "PUNE", "PHEK", "EAST", "WEST", "PURI", "MAHE", "MOGA", "KOTA", "PALI", "TONK", "AGRA", "ETAH"),
        Arrays.asList("ANJAW", "LOHIT", "SIANG", "TIRAP", "BAKSA", "HOJAI", "ARWAL", "BANKA", "BUXAR", "JAMUI", "PATNA", "SARAN", "SIWAN", "BALOD", "KORBA", "SUKMA", "DAMAN", "ANAND", "BOTAD", "DOHAD", "KHEDA", "MORBI", "PATAN", "SURAT", "HISAR", "MEWAT", "SIRSA", "KULLU", "MANDI", "SOLAN", "JAMMU", "PUNCH", "REASI", "SAMBA", "DUMKA", "GODDA", "GUMLA", "PAKUR", "BIDAR", "GADAG", "KOLAR", "UDUPI", "BETUL", "BHIND", "DAMOH", "DATIA", "DEWAS", "HARDA", "KATNI", "PANNA", "SAGAR", "SATNA",
                                "SEONI", "SIDHI", "AKOLA", "DHULE", "JALNA", "LATUR", "THANE", "NONEY", "MAMIT", "SAIHA", "PEREN", "WOKHA", "NORTH", "SOUTH", "BAUDH", "YANAM", "MANSA", "AJMER", "ALWAR", "BARAN", "BUNDI", "CHURU", "DAUSA", "JALOR", "SIKAR", "ERODE", "KARUR", "SALEM", "THENI", "MEDAK", "BANDA", "BASTI", "GONDA", "HAPUR", "KHERI", "UNNAO", "NADIA"),
        Arrays.asList("GUNTUR", "Y.S.R.", "NAMSAI", "TAWANG", "CACHAR", "DHUBRI", "JORHAT", "KAMRUP", "MAJULI", "NAGAON", "ARARIA", "MUNGER", "NAWADA", "PURNIA", "ROHTAS", "SUPAUL", "BASTAR", "KORIYA", "RAIPUR", "AMRELI", "RAJKOT", "VALSAD", "AMBALA", "KARNAL", "PALWAL", "REWARI", "ROHTAK", "CHAMBA", "KANGRA", "SHIMLA", "BADGAM", "KARGIL", "KATHUA", "KULGAM", "RAMBAN", "BOKARO", "CHATRA", "GARHWA", "KHUNTI", "PALAMU", "RANCHI", "HASSAN", "HAVERI", "KODAGU", "KOPPAL", "MANDYA", "MYSORE",
                                 "TUMKUR", "YADGIR", "IDUKKI", "KANNUR", "KOLLAM", "BHOPAL", "INDORE", "JHABUA", "MANDLA", "MORENA", "RAISEN", "RATLAM", "SEHORE", "UJJAIN", "UMARIA", "MUMBAI", "NAGPUR", "NANDED", "NASHIK", "SANGLI", "SATARA", "WARDHA", "WASHIM", "UKHRUL", "RIBHOI", "AIZAWL", "KOHIMA", "ANUGUL", "GANJAM", "BARMER", "JAIPUR", "NAGAUR", "SIROHI", "NIRMAL", "DHALAI", "GOMATI", "KHOWAI", "AMETHI", "AMROHA", "BALLIA", "BIJNOR", "BUDAUN", "DEORIA", "ETAWAH", "HARDOI", "JALAUN",
                                 "JHANSI", "MAHOBA", "MEERUT", "RAMPUR", "SHAMLI", "ALMORA", "HOWRAH", "MALDAH"),
        Arrays.asList("KRISHNA", "KURNOOL", "BARPETA", "CHIRANG", "DARRANG", "DHEMAJI", "NALBARI", "BHOJPUR", "KATIHAR", "NALANDA", "SAHARSA", "SHEOHAR", "BIJAPUR", "JASHPUR", "MUNGELI", "RAIGARH", "SURGUJA", "ARVALLI", "BHARUCH", "KACHCHH", "NARMADA", "NAVSARI", "BHIWANI", "GURGAON", "JHAJJAR", "KAITHAL", "PANIPAT", "SONIPAT", "KINNAUR", "SIRMAUR", "KUPWARA", "PULWAMA", "RAJOURI", "DEOGHAR", "DHANBAD", "GIRIDIH", "JAMTARA", "KODARMA", "LATEHAR", "RAMGARH", "SIMDEGA", "BELGAUM", "BELLARY",
                                  "BIJAPUR", "DHARWAD", "RAICHUR", "SHIMOGA", "WAYANAD", "ANUPPUR", "BARWANI", "DINDORI", "GWALIOR", "NEEMUCH", "RAJGARH", "SHAHDOL", "SHAJPUR", "SHEOPUR", "VIDISHA", "BULDANA", "GONDIYA", "HINGOLI", "JALGAON", "PALGHAR", "RAIGARH", "SOLAPUR", "CHANDEL", "JIRIBAM", "KAMJONG", "THOUBAL", "KOLASIB", "LUNGLEI", "DIMAPUR", "KIPHIRE", "CENTRAL", "BARGARH", "BHADRAK", "CUTTACK", "JAJAPUR", "KHORDHA", "KORAPUT", "NUAPADA", "BARNALA", "FAZILKA", "MUKTSAR", "PATIALA",
                                  "SANGRUR", "BIKANER", "JODHPUR", "KAULAUR", "UDAIPUR", "CHENNAI", "MADURAI", "VELLORE", "JAGTIAL", "JANGOAN", "KHAMMAM", "UNAKOTI", "ALIGARH", "AURAIYA", "BAGHPAT", "HATHRAS", "JAUNPUR", "KANNAUJ", "KASGANJ", "LUCKNOW", "MATHURA", "SAMBHAL", "SITAPUR", "CHAMOLI", "BANKURA", "BIRBHUM", "HOOGHLY", "KOLKATA", "PURULIA"),
        Arrays.asList("NICOBARS", "CHITTOOR", "PRAKASAM", "GOALPARA", "GOLAGHAT", "MORIGAON", "SONITPUR", "TINSUKIA", "UDALGURI", "KHAGARIA", "VAISHALI", "BEMETARA", "BILASPUR", "DHAMTARI", "SURAJPUR", "JAMNAGAR", "JUNAGADH", "MAHESANA", "VADODARA", "BILASPUR", "HAMIRPUR", "ANANTNAG", "BARAMULA", "KISHTWAR", "SHUPIYAN", "SRINAGAR", "UDHAMPUR", "BAGALKOT", "GULBARGA", "KOTTAYAM", "PALAKKAD", "THRISSUR", "BALAGHAT", "JABALPUR", "MANDSAUR", "SHIVPURI", "AMRAVATI", "BHANDARA", "KOLHAPUR",
                                   "PARBHANI", "YAVATMAL", "KAKCHING", "PHERZAWL", "SENAPATI", "CHAMPHAI", "SERCHHIP", "LONGLeng", "TUENsang", "SHAHdara", "BALANGIR", "DEBAGARH", "GAJAPATI", "NAYAGARH", "RAYAGADA", "KARAIKAL", "AMRITSAR", "BATHINDA", "FARIDKOT", "FIROZPUR", "LUDHIANA", "RUPNAGAR", "BANSWARA", "BHILWARA", "DHAULPUR", "JHALAWAR", "ARIYALUR", "DINDIGUL", "NAMAKKAL", "TIRUPPUR", "ADILABAD", "NALGONDA", "SIDDIPET", "SURYAPET", "AZAMGARH", "BAHRAICH", "BAREILLY", "FAIZABAD",
                                   "FATEHPUR", "GHAZIPUR", "LALITPUR", "MAINPURI", "MIRZAPUR", "PILIBHIT", "VARANASI", "DEHRADUN", "HARIDWAR", "NAINITAL", "JHARGRAM"),
        Arrays.asList("ANANTAPUR", "CHANGLANG", "KRA DAADI", "BISWANATH", "CHARAIDEO", "DIBRUGARH", "KARIMGANJ", "KOKRAJHAR", "LAKHIMPUR", "SIVASAGAR", "BEGUSARAI", "BHAGALPUR", "DARBHANGA", "GOPALGANJ", "JEHANABAD", "MADHEPURA", "MADHUBANI", "SITAMARHI", "BALRAMPUR", "KONDAGAON", "NORTH GOA", "SOUTH GOA", "AHMADABAD", "BHAVNAGAR", "MAHISAGAR", "PORBANDAR", "THE DANGS", "FARIDABAD", "FATEHABAD", "PANCHKULA", "BANDIPORA", "GANDERBAL", "LOHARDAGA", "SAHIBGANJ", "BANGALORE", "ALAPPUZHA",
                                    "ERNAKULAM", "KASARAGOD", "KOZHIKODE", "ALIRAJPUR", "BURHANPUR", "SINGRAULI", "TIKAMGARH", "NANDURBAR", "OSMANABAD", "RATNAGIRI", "BISHNUPUR", "KANGPOKPI", "LAWNGTLAI", "ZUNHEBOTO", "NEW DELHI", "BALESHWAR", "DHENKANAL", "KALAHANDI", "KANDHAMAL", "KENDUJHAR", "SAMBALPUR", "GURDASPUR", "JALANDHAR", "PATHANKOT", "BHARATPUR", "DUNGARPUR", "JAISALMER", "RAJSAMAND", "CUDDALORE", "SIVAGANGA", "THANJAVUR", "HYDERABAD", "KAMAREDDY", "NIZAMABAD", "VIKARABAD",
                                    "ALLAHABAD", "BARABANKI", "CHANDAULI", "FIROZABAD", "GHAZIABAD", "GORAKHPUR", "KAUSHAMBI", "MORADABAD", "SHRAVASTI", "SONBHADRA", "SULTANPUR", "BAGESHWAR", "CHAMPAWAT", "KALIMPONG"),
        Arrays.asList("SRIKAKULAM", "EAST SIANG", "PAPUM PARE", "WEST SIANG", "BONGAIGAON", "DIMA HASAO", "HAILAKANDI", "AURANGABAD", "KISHANGANJ", "LAKHISARAI", "SAMASTIPUR", "SHEIKHPURA", "CHANDIGARH", "GARIYABAND", "KABEERDHAM", "MAHASAMUND", "NARAYANPUR", "HAZARIBAGH", "DAVANAGERE", "RAMANAGARA", "MALAPPURAM", "AGAR MALWA", "ASHOKNAGAR", "CHHATARPUR", "CHHINDWARA", "AHMADNAGAR", "AURANGABAD", "CHANDRAPUR", "GADCHIROLI", "SINDHUDURG", "TAMENGLONG", "TENGNoupal", "MOKOKCHUNG", "NORTH EAST",
                                     "NORTH WEST", "SOUTH WEST", "JHARSUGUDA", "KENDRAPARA", "MALKANGIRI", "MAYURBHANJ", "SUBARNAPUR", "SUNDARGARH", "PUDUCHERRY", "HOSHIARPUR", "KAPURTHALA", "TARN TARAN", "JHUNJHUNUN", "PRATAPGARH", "COIMBATORE", "DHARMAPURI", "PERAMBALUR", "THIRUVARUR", "VILUPPURAM", "KARIMNAGAR", "MAHBUBABAD", "MANCHERIAL", "PEDDAPALLI", "SANGAREDDY", "WANAPARTHY", "SEPAHIJALA", "CHITRAKOOT", "RAE BARELI", "SAHARANPUR", "UTTARKASHI", "ALIPURDUAR", "COOCHBEHAR", "DARJEELING", "JALPAIGURI"),
        Arrays.asList("EAST KAMENG", "LOWER SIANG", "UPPER SIANG", "WEST KAMENG", "MUZAFFARPUR", "RAJNANDGAON", "GANDHINAGAR", "GIR SOMNATH", "KURUKSHETRA", "YAMUNANAGAR", "LAHUL SPITI", "LEH(LADAKH)", "CHIKMAGALUR", "CHITRADURGA", "LAKSHADWEEP", "HOSHANGABAD", "NARSIMHAPUR", "IMPHAL EAST", "IMPHAL WEST", "HANUMANGARH", "KRISHNAGIRI", "PUDUKKOTTAI", "THIRUVALLUR", "TIRUNELVELI", "MAHBUBNAGAR", "RANGA REDDY", "BULANDSHAHR", "FARRUKHABAD", "KUSHI NAGAR", "MAHARAJGANJ", "PITHORAGARH", "MURSHIDABAD"),
        Arrays.asList("VIZIANAGARAM", "KURUNG KUMEY", "BALODA BAZAR", "BANAS KANTHA", "PANCH MAHALS", "SABAR KANTHA", "MAHENDRAGARH", "NABARANGAPUR", "CHITTAURGARH", "KANCHEEPURAM", "NAGAPATTINAM", "THE NILGIRIS", "THOOTHUKKUDI", "VIRUDHUNAGAR", "NAGARKURNOOL", "WEST TRIPURA", "KANPUR DEHAT", "KANPUR NAGAR", "SHAHJAHANPUR", "RUDRA PRAYAG"),
        Arrays.asList("SOUTH ANDAMAN", "EAST GODAVARI", "VISAKHAPATNAM", "WEST GODAVARI", "DIBANG VALLEY", "KARBI ANGLONG", "CHHOTA UDEPUR", "SURENDRANAGAR", "CHARKHI DADRI", "CHURACHANDPUR", "JAINTIA HILLS", "EAST DISTRICT", "WEST DISTRICT", "KANNIYAKUMARI", "NORTH TRIPURA", "SOUTH TRIPURA", "MUZAFFARNAGAR", "PAURI GARHWAL", "TEHRI GARHWAL"),
        Arrays.asList("CHAMARAJANAGAR", "UTTARA KANNADA", "PATHANAMTHITTA", "JAGATSINGHAPUR", "SAWAI MADHOPUR", "SRI GANGANAGAR", "SOUTH DISTRICT", "RAMANATHAPURAM", "TIRUVANNAMALAI", "AMBEDKAR NAGAR", "SIDDHARTHNAGAR", "DINAJPUR UTTAR", "MEDINIPUR EAST", "MEDINIPUR WEST"),
        Arrays.asList("LOWER SUBANSIRI", "UPPER SUBANSIRI", "KAIMUR (BHABUA)", "PURBI CHAMPARAN", "PURBI SINGHBHUM", "BANGALORE RURAL", "CHIKKABALLAPURA", "MUMBAI SUBURBAN", "EAST GARO HILLS", "WEST GARO HILLS", "FATEHGARH SAHIB", "NORTH  DISTRICT", "TIRUCHIRAPPALLI"),
        Arrays.asList("JANJGIR - CHAMPA", "DEVBHOOMI DWARKA", "DAKSHINA KANNADA", "EAST KHASI HILLS", "NORTH GARO HILLS", "SOUTH GARO HILLS", "WEST KHASI HILLS", "SOUTH EAST DELHI", "RAJANNA SIRCILLA", "WARANGAL (RURAL)", "WARANGAL (URBAN)", "SANT KABIR NAGAR", "UDAM SINGH NAGAR", "DINAJPUR DAKSHIN"),
        Arrays.asList("JOGLULAMBA GADWAL"),
        Arrays.asList("WEST KARBI ANGLONG", "PASHCHIM CHAMPARAN", "THIRUVANANTHAPURAM", "EAST JAINTIA HILLS", "WEST JAINTIA HILLS"),
        Arrays.asList("LOWER DIBANG VALLEY", "KAMRUP METROPOLITAN", "UTTAR BASTAR KANKER", "PASHCHIMI SINGHBHUM", "SARAIKELA-KHARSAWAN", "MEDCHAL-MALKAJIGIRI", "YADADRI BHUVANAGIRI", "GAUTAM BUDDHA NAGAR"),
        Arrays.asList("KHANDWA (EAST NIMAR)", "BHADRADRI KOTHAGUDEM"),
        Arrays.asList("KHARGONE (WEST NIMAR)", "SOUTH WEST GARO HILLS"),
        Arrays.asList("DADRA AND NAGAR HAVELI", "SOUTH WEST KHASI HILLS", "KUMURAM BHEEM ASIFABAD"),
        Arrays.asList(" "),
        Arrays.asList("NORTH AND MIDDLE ANDAMAN", "SOUTH SALAMARA-MANKACHAR", "DAKSHIN BASTAR DANTEWADA", "JAYASHANKAR BHUPALAPALLI"),
        Arrays.asList("SHAHID BHAGAT SINGH NAGAR"),
        Arrays.asList("SAHIBZADA AJIT SINGH NAGAR"),
        Arrays.asList("SRI POTTI SRIRAMULU NELLORE")
));
    private static final List<String> unionterritories = new ArrayList<>(Arrays.asList("ANDAMAN AND NICOBAR ISLANDS", "CHANDIGARH", "DADRA AND NAGAR HAVELI AND DAMAN AND DIU", "LAKSHADWEEP", "DELHI", "PUDUCHERRY", "LADAKH", "JAMMU AND KASHMIR"));


    public List<String> extractColumnNames(MultipartFile uploadedfile) throws IOException {
        file = uploadedfile;
        filename = uploadedfile.getOriginalFilename();
        workbook = WorkbookFactory.create(file.getInputStream());
        sheet = workbook.getSheetAt(0);
        headerRow = sheet.getRow(0);
        columns=new ArrayList<>();
        for (Cell cell : headerRow) if(cell.getCellType()!=CellType.BLANK) columns.add(cell.getStringCellValue());
        workbook.close();
        return columns;
    }

    private static final List<String> railwayCodes = new ArrayList<>(Arrays.asList("CR","ER","ECR","ECOR","NR","NCR","NER","NFR","NWR","SR","SCR","SER","SECR","SWR","WR","WCR","MRK"));
    private static final List<String> states = new ArrayList<>(Arrays.asList("ANDHRA PRADESH", "ARUNACHAL PRADESH", "ASSAM", "BIHAR", "CHHATTISGARH", "GOA", "GUJARAT", "HARYANA", "HIMACHAL PRADESH", "JHARKHAND", "KARNATAKA", "KERALA", "MADHYA PRADESH", "MAHARASHTRA", "MANIPUR", "MEGHALAYA", "MIZORAM", "NAGALAND", "ODISHA", "PUNJAB", "RAJASTHAN", "SIKKIM", "TAMIL NADU", "TELANGANA", "TRIPURA", "UTTAR PRADESH", "UTTARAKHAND", "WEST BENGAL"));

    public Map<String, Boolean> calculateFileFormat(String selectedColumns) {
        formatCheck="file";
        attributes = selectedColumns;
        List<Integer> l=new ArrayList<>();
        Map<Integer, Set<String>> data = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        for(int i=0; i<columns.size(); i++) if(selectedColumns.contains(columns.get(i))) {l.add(i);data.put(i, new TreeSet<>());map.put(columns.get(i), true);}
        int total=sheet.getPhysicalNumberOfRows()-1;
        for(int i=1; i<total; i++) {
            headerRow = sheet.getRow(i);
            for(int j=0; j<l.size(); j++){
                Cell cell = headerRow.getCell(l.get(j));
                if(cell == null || cell.getCellType()== CellType.BLANK)map.put(columns.get(l.get(j)), false);
                else {
                    String value = cell.getCellType()==CellType.NUMERIC ? ""+cell.getNumericCellValue() : cell.getStringCellValue();
//                    System.out.println(value);
                    Set<String> s = data.get(l.get(j));
                    if (s.contains(value) || test.contains(value.toUpperCase()) || (value.length()==1 && (value.charAt(0)<49 || value.charAt(0)>57) && (value.charAt(0)<65 || value.charAt(0)>90) && (value.charAt(0)<97 || value.charAt(0)>122))) {
                        map.put(columns.get(l.get(j)), false);
//                        System.out.println(value);
                        l.remove(j--);
                    } else {
                        s.add(value);
                        data.put(l.get(j), s);
                    }
                }
            }
        }
        this.data=map.toString();
//        System.out.println(data);
//        System.out.println(map);
        return map;
    }


    public Map<String, Double> calculateDateFormat(String selectedColumns){
        formatCheck="date";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, n=3, total = sheet.getPhysicalNumberOfRows()-1;
//        System.out.println(total);
        for (int i = 1; i<total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK) {if (--n < 0) total = i - 4;}
                else switch (cell.getCellType()) {
                    case NUMERIC:
//                        System.out.println(cell.getDateCellValue()+" n "+i);
                        if (!DateUtil.isCellDateFormatted(cell)) {
                            invalid++;
                            cell.getDateCellValue();
                        }
                        break;
                    case STRING:
//                        System.out.println(cell.getStringCellValue()+" s "+i);
                        if (!validateAndConvertDate(cell.getStringCellValue().split(" ")[0])) invalid++;
                        break;
                }
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }private Boolean validateAndConvertDate(String inputDate) {
        if (inputDate == null || inputDate.trim().isEmpty()) {
            return false;
        }
        inputDate = inputDate.split(" ")[0];
        try {
            LocalDate date = LocalDate.parse(inputDate, perfectFormatter);
//            System.out.println(inputDate+" "+date);
            return date.getYear() >= 1900 && date.getYear() <= 2050;
        } catch (DateTimeParseException e) {
            String converted = tryParseAndConvert(inputDate);
//            System.out.println(inputDate+" "+converted);
            return converted != null;
        }
    }private String tryParseAndConvert(String inputDate) {
        for (String format : dateFormats) {
            try {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern(format));
//                System.out.println(date);
                return date.format(perfectFormatter);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        return null;
    }
    public Map<String, Double> calculateStationcodeFormat(String selectedColumns){
        formatCheck = "stationcode";
        attributes=selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, n = 3, total = sheet.getPhysicalNumberOfRows() - 1;
        System.out.println(total);
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType()==CellType.BLANK) {if (--n < 0) total = i - 3;}
                else if (cell.getCellType() != CellType.STRING || cell.getStringCellValue().length() < 2 || cell.getStringCellValue().length() > 4 || !stationCodes.get(cell.getStringCellValue().length()).contains(cell.getStringCellValue())) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }
    public Map<String, Double> calculateLatlongFormat(String selectedColumns) throws IOException {
        formatCheck = "latlong";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, total = sheet.getPhysicalNumberOfRows() - 1;
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK || cell.getCellType() != CellType.STRING || test.contains(cell.getStringCellValue()) || !validateLatLongFormat(cell.getStringCellValue())) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }
    public Boolean validateLatLongFormat(String value){
        char z=value.charAt(value.length()-1);
        if(z!='N' && z!='E' && z!='W' && z!='S') return false;
        String s="";
        int x=4;
        double x3=0.0;
        for(int k=0; k<value.length()-1 && x>0; k++) {
            char c = value.charAt(k);
            if (c > 47 && c < 58) s+=c;
            else {
                switch (x--) {
                    case 4:
                        if (c != '°') return false;
                        else {x3+=Integer.parseInt(s);s="";}
                        break;
                    case 3:
                        if (c != '\'') return false;
                        else {x3+=Double.parseDouble(s)/60.0;s="";}
                        break;
                    case 2:
                        if (c != '.') return false;
                        else s+=c;
                        break;
                    case 1:
                        if (c != '"') return false;
                        else {x3+=Double.parseDouble(s)/3600.0;s="";}
                        break;
                }
            }
        }
        return x3 <= 90;
    }
    public Map<String, Double> calculateRailwaycodeFormat(String selectedColumns){
        formatCheck = "railwaycode";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, total = sheet.getPhysicalNumberOfRows() - 1, n=3;
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK) {if (--n == 0) total = i - 3;}
                else if (cell.getCellType() != CellType.STRING || cell.getStringCellValue().length() < 2 || cell.getStringCellValue().length() > 4 || !railwayCodes.contains(cell.getStringCellValue())) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }
    public Map<String, Double> calculatePincodeFormat(String selectedColumns){
        formatCheck = "pincode";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, total = sheet.getPhysicalNumberOfRows() - 1,n=5;
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK) {if (--n == 0) total = i - 5;}
                else if (cell.getCellType() != CellType.NUMERIC || ("" + (int)cell.getNumericCellValue()).length() != 6) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }
    public Map<String, Double> calculateStateFormat(String selectedColumns){
        formatCheck = "state";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, total = sheet.getPhysicalNumberOfRows() - 1, n=5;
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK) {if (--n == 0) total = i - 5;}
                else if (cell.getCellType() != CellType.STRING || cell.getStringCellValue().length() < 2 || cell.getStringCellValue().length() > 15 || !states.contains(cell.getStringCellValue())) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }
    public Map<String, Double> calculateDistrictFormat(String selectedColumns){
        formatCheck = "district";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, total = sheet.getPhysicalNumberOfRows() - 1, n=5;
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK) {if (--n == 0) total = i - 5;}
                else if (cell.getCellType() != CellType.STRING || cell.getStringCellValue().length() < 3 || cell.getStringCellValue().length() > 27 || !districts.get(cell.getStringCellValue().length()).contains(cell.getStringCellValue().toUpperCase())) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }
    public Map<String, Double> calculateUnionterritoriesFormat(String selectedColumns){
        formatCheck = "unionterritories";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, total = sheet.getPhysicalNumberOfRows() - 1,n=5;
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK) {if (--n == 0) total = i - 5;}
                else if (cell.getCellType() != CellType.STRING || cell.getStringCellValue().length() < 2 || cell.getStringCellValue().length() > 15 || !unionterritories.contains(cell.getStringCellValue())) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }

    public Map<String, Double> calculatePhonenumFormat(String selectedColumns){
        formatCheck = "phonenum";
        attributes = selectedColumns;
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) if (selectedColumns.contains(columns.get(i))) l.add(i);
        int invalid = 0, total = sheet.getPhysicalNumberOfRows() - 1, n=5;
        for (int i = 1; i < total; i++) {
            headerRow = sheet.getRow(i);
            for (int j : l) {
                Cell cell = headerRow.getCell(j);
                if (cell == null || cell.getCellType() == CellType.BLANK) {if (--n == 0) total = i - 3;}
                else if (cell.getCellType() != CellType.NUMERIC || ("" + (int) cell.getNumericCellValue()).length() != 10) invalid++;
            }
        }
        errorRate = ((double) Math.round(10000.0 * invalid / total / l.size()) / 100);
        System.out.println(invalid);
        System.out.println(total);
        System.out.println(l.size());
        System.out.println(errorRate);
        return Map.of("errorRate", errorRate);
    }


    //Save Format
    public void saveFormat(String format) {
        repositoryf.save(new Format(filename, attributes, errorRate, data, formatCheck, LocalDateTime.now()));
    }

    //Get All Format
    public List<Format> getByFormat(String format) {
        return repositoryf.findByFormatCheck(format);
    }
    public List<Format> getAllFormats() {
        return repositoryf.findAll();
    }

    //Delete Formats By Id
    public void deleteFormatById(Long id) {
        repositoryf.deleteById(id);
    }


    //Delete Formats By FormatCheck
    public void deleteFormatByFormatCheck(String formatCheck) {
        repositoryf.deleteByFormatCheck(formatCheck);
    }
}
