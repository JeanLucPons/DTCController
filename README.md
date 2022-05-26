# DTCController

DTCController is a java application that allows to edit [DTC](https://en.wikipedia.org/wiki/OBD-II_PIDs) (Diagnostic Trouble Codes) table. It supports only EDC16C34 PSA platforms at the moment.
The program tries to find the addresses of the needed tables using the strategy described bellow. It will work only on 
PSA platform but can be extended to others vehicle. All contributions to this project are welcome.

It is mandatory to open an original dump (not necessary a full dump) to find addresses. Then, if you think the algorithm did
a good job you can add your config to the list of know projects in the Dump.findDTCInfo() method. All projects 
share same addresses independently of the software number. The project is the identifier of the DaMOS (DAtabase for 
Microcontroller-Oriented Systems) A2L description file [(ASAM-MCD-2MC)](https://www.asam.net/standards/detail/mcd-2-mc/) which contains characteristic addresses and descriptions.

**Important:** DTCController does not compute any checksum, ensure that your OBD ECU programmer or your BDM computes the checksum for the modified file.

# How to use

- Download source code in a directory named DTCController or clone the repository using:
```
 git clone https://github.com/JeanLucPons/DTCController.git
```
- Install java JDK (either [openJDK](https://openjdk.java.net/) or [OracleJDK](https://www.oracle.com/java/technologies/downloads/))
- Execute in a command prompt or in a shell:
```
cd [my_path]/DTCController
javac *.java
cd ..
java DTCController.EDC16C34
```
This will compile and execute the program, if you make modifcation of the source code, you need to recompile using javac.

![](https://github.com/JeanLucPons/DTCController/blob/main/docs/sccr1.jpg)

You can also download the executable and precompiled jar file from [releases](https://github.com/JeanLucPons/DTCController/releases) and create a shorcut on it as below. Change the path to the JDK (or JRE) you are using:

```
Target: "C:\Program Files\jdk-18.0.1.1\bin\javaw.exe" -jar D:\Downloads\DTCController.jar
```
![](https://github.com/JeanLucPons/DTCController/blob/main/docs/shorcut.jpg)

To disable a DTC, select disable in the class column (it disables the whole line). To disalbe only one DTC in the line, click on the Env button, and select "Sig unused" for the appropriate DTC. If you change settings of a DTC class, it will be applied to all DTC that belong to this class. The program outputs memory changes on the console.

# How it works

The address search algorithm is quite simple, it assumes that the first code of the internal code table is P0530 
(Address of DSM_CDKDfp_ACCDPresAna_C) and the last one is P1621 (Address of DSM_CDKDfp_WdCom_C). The algorithm can be found in Dump.findDTCInfo() method.
When the A2L file is generated, fault characteristics are sorted in alphabetic order (case sensitive). It is unlikely that a fault characteristic is added before DSM_CDKDfp_ACCDPresAna_C or after DSM_CDKDfp_WdCom. However, it might fail. To check if the algorithm did a good job, check the correspondance of DTC internal code and Code #1,#2,#3 and #4. If they are coherent, then you can relie on the result. For instance, on the above example, you can see that P0530 is well related to P0532 and P0533 (Air Conditioning faults).

**Note** Even for a same engine, a same EDC16C34, DTC table may be different (size and/or content and/or addresses).

Here is an exemple with a known A2L file for a 206 1.6 HDI 110HP (Project C35374A).

|Idx|Address|Internal code|Characteristic name|
|:--|:-----|:---|:---|
|000|1C65AE|P0530|DSM_CDKDfp_ACCDPresAna_C|
|001|1C65B0|P1506|DSM_CDKDfp_ACCDSwtin_C|
|002|1C65B2|P0610|DSM_CDKDfp_ADCMon_C|
|003|1C65B4|P0102|DSM_CDKDfp_AFSCDAdjVal_C|
|004|1C65B6|P0103|DSM_CDKDfp_AFSCDLrnVal_C|
|005|1C65B8|P0111|DSM_CDKDfp_AFSCD_AirTemp_C|
|006|1C65BA|P0000|DSM_CDKDfp_AFSCD_AirTempDcyc_C|
|007|1C65BC|P0104|DSM_CDKDfp_AFSCD_PlOffsDrft_C|
|008|1C65BE|P0101|DSM_CDKDfp_AFSCD_PlSetyDrftHi_C|
|009|1C65C0|P0109|DSM_CDKDfp_AFSCD_PlSetyDrftLo_C|
|010|1C65C2|P0111|DSM_CDKDfp_AFSCD_SRCAirTemp_C|
|011|1C65C4|P0100|DSM_CDKDfp_AFSCD_SRCAirm_C|
|012|1C65C6|P0105|DSM_CDKDfp_AFSCD_SRCBatt_C|
|013|1C65C8|P0106|DSM_CDKDfp_AFSCD_SRCCorrAirm_C|
|014|1C65CA|P0107|DSM_CDKDfp_AFSCD_SRCRawAirm_C|
|015|1C65CC|P0108|DSM_CDKDfp_AFSCD_SRCRefSigPer_C|
|016|1C65CE|P1403|DSM_CDKDfp_AOHtCDHt1_C|
|017|1C65D0|P1404|DSM_CDKDfp_AOHtCDHt2_C|
|018|1C65D2|P0220|DSM_CDKDfp_APP1_C|
|019|1C65D4|P0225|DSM_CDKDfp_APP2_C|
|020|1C65D6|P1614|DSM_CDKDfp_APPCDKickDwnSens_C|
|021|1C65D8|P1101|DSM_CDKDfp_APSCD_C|
|022|1C65DA|P1628|DSM_CDKDfp_AccPedPlausBrk_C|
|023|1C65DC|P0000|DSM_CDKDfp_AddPCD_Max_C|
|024|1C65DE|P0000|DSM_CDKDfp_AddPCD_Min_C|
|025|1C65E0|P0000|DSM_CDKDfp_AddPCD_SigNpl_C|
|026|1C65E2|P1435|DSM_CDKDfp_AddPmpCAN1_C|
|027|1C65E4|P1436|DSM_CDKDfp_AddPmpCAN2_C|
|028|1C65E6|P1445|DSM_CDKDfp_AddPmpCANVol_C|
|029|1C65E8|P1442|DSM_CDKDfp_AddPmpLvl_C|
|030|1C65EA|P1446|DSM_CDKDfp_AddPmpLvlCrit_C|
|031|1C65EC|P2413|DSM_CDKDfp_AirCtlFlowChk_C|
|032|1C65EE|P0401|DSM_CDKDfp_AirCtlGvnrDvtMax_C|
|033|1C65F0|P0402|DSM_CDKDfp_AirCtlGvnrDvtMin_C|
|034|1C65F2|P0404|DSM_CDKDfp_AirSys_AirmPlSpd_C|
|035|1C65F4|P1505|DSM_CDKDfp_AirbCD_C|
|036|1C65F6|P1410|DSM_CDKDfp_ArHtCD_Max_C|
|037|1C65F8|P1409|DSM_CDKDfp_ArHtCD_Min_C|
|038|1C65FA|P0403|DSM_CDKDfp_ArHtCD_SigNpl_C|
|039|1C65FC|P0246|DSM_CDKDfp_BPACD_Max_C|
|040|1C65FE|P0245|DSM_CDKDfp_BPACD_Min_C|
|041|1C6600|P0243|DSM_CDKDfp_BPACD_SigNpl_C|
|042|1C6602|P0235|DSM_CDKDfp_BPSCD_C|
|043|1C6604|P0560|DSM_CDKDfp_BattCD_C|
|044|1C6606|P0571|DSM_CDKDfp_BrkCD_C|
|045|1C6608|P1151|DSM_CDKDfp_CABCD_Max_C|
|046|1C660A|P1154|DSM_CDKDfp_CABCD_Min_C|
|047|1C660C|P1150|DSM_CDKDfp_CABCD_SigNpl_C|
|048|1C660E|P1156|DSM_CDKDfp_CABSCD_C|
|049|1C6610|P1466|DSM_CDKDfp_CABSCD_JamVlv_C|
|050|1C6612|P1465|DSM_CDKDfp_CABSCD_LgTimeDrft_C|
|051|1C6614|P1109|DSM_CDKDfp_CABSCD_ShTimeDrft_C|
|052|1C6616|P1152|DSM_CDKDfp_CABVlvGvnrDvt_C|
|053|1C6618|P1159|DSM_CDKDfp_CABVlvJamVlv_C|
|054|1C661A|P0115|DSM_CDKDfp_CTSCD_C|
|055|1C661C|P0116|DSM_CDKDfp_Clg_DynTst_C|
|056|1C661E|P0301|DSM_CDKDfp_CmbChbMisfire1_C|
|057|1C6620|P0302|DSM_CDKDfp_CmbChbMisfire2_C|
|058|1C6622|P0303|DSM_CDKDfp_CmbChbMisfire3_C|
|059|1C6624|P0304|DSM_CDKDfp_CmbChbMisfire4_C|
|060|1C6626|P0305|DSM_CDKDfp_CmbChbMisfire5_C|
|061|1C6628|P0306|DSM_CDKDfp_CmbChbMisfire6_C|
|062|1C662A|P0300|DSM_CDKDfp_CmbChbMisfireMul_C|
|063|1C662C|P1199|DSM_CDKDfp_CoEng_FlMin_C|
|064|1C662E|P1625|DSM_CDKDfp_CoVMDCSh_C|
|065|1C6630|P0704|DSM_CDKDfp_ConvCD_C|
|066|1C6632|P1671|DSM_CDKDfp_CrCCDKey_C|
|067|1C6634|P0114|DSM_CDKDfp_EATSCD_C|
|068|1C6636|P1600|DSM_CDKDfp_EEPCDQntAdj_C|
|069|1C6638|P1613|DSM_CDKDfp_EEPCDVarMng_C|
|070|1C663A|P0403|DSM_CDKDfp_EGRCD_Sig_C|
|071|1C663C|P0405|DSM_CDKDfp_EGRSCD_C|
|072|1C663E|P1462|DSM_CDKDfp_EGRSCD_JamVlv_C|
|073|1C6640|P1461|DSM_CDKDfp_EGRSCD_LgTimeDrft_C|
|074|1C6642|P1100|DSM_CDKDfp_EGRSCD_ShTimeDrft_C|
|075|1C6644|P1162|DSM_CDKDfp_EGRVlvGvnrDvtMax_C|
|076|1C6646|P1162|DSM_CDKDfp_EGRVlvGvnrDvtMin_C|
|077|1C6648|P1163|DSM_CDKDfp_EGRVlv_JamVlv_C|
|078|1C664A|P0340|DSM_CDKDfp_EngMCaS1_C|
|079|1C664C|P0341|DSM_CDKDfp_EngMCaS2_C|
|080|1C664E|P0335|DSM_CDKDfp_EngMCrS1_C|
|081|1C6650|P0336|DSM_CDKDfp_EngMCrS2_C|
|082|1C6652|P1727|DSM_CDKDfp_FMTC_NonMonotonMap_C|
|083|1C6654|P0180|DSM_CDKDfp_FTSCD_C|
|084|1C6656|P0483|DSM_CDKDfp_FanCD_C|
|085|1C6658|P0480|DSM_CDKDfp_FanCDOut1_C|
|086|1C665A|P0481|DSM_CDKDfp_FanCDOut2_C|
|087|1C665C|P0000|DSM_CDKDfp_FlSysDetRefuel_C|
|088|1C665E|U0028|DSM_CDKDfp_FrmMngEOBD_C|
|089|1C6660|P1800|DSM_CDKDfp_FrmMngESPChk1_C|
|090|1C6662|P1801|DSM_CDKDfp_FrmMngESPChk2_C|
|091|1C6664|P1802|DSM_CDKDfp_FrmMngESPChk3_C|
|092|1C6666|P1803|DSM_CDKDfp_FrmMngESPChk4_C|
|093|1C6668|P1804|DSM_CDKDfp_FrmMngESPChk5_C|
|094|1C666A|P0656|DSM_CDKDfp_FrmMngFlLvl_C|
|095|1C666C|U0404|DSM_CDKDfp_FrmMngTC_C|
|096|1C666E|P1728|DSM_CDKDfp_FrmMngTrqInval_C|
|097|1C6670|P1300|DSM_CDKDfp_GlwCD_Actr_C|
|098|1C6672|P0000|DSM_CDKDfp_GlwCD_Lamp_C|
|099|1C6674|P0382|DSM_CDKDfp_GlwCtl1_C|
|100|1C6676|P0380|DSM_CDKDfp_GlwCtl2_C|
|101|1C6678|P0611|DSM_CDKDfp_HWEMonCom_C|
|102|1C667A|P0603|DSM_CDKDfp_HWEMonEEPROM_C|
|103|1C667C|P0604|DSM_CDKDfp_HWEMonRcyLocked_C|
|104|1C667E|P0605|DSM_CDKDfp_HWEMonRcySuppressed_C|
|105|1C6680|P0606|DSM_CDKDfp_HWEMonRcyVisible_C|
|106|1C6682|P0620|DSM_CDKDfp_HWEMonUMaxSupply_C|
|107|1C6684|P0621|DSM_CDKDfp_HWEMonUMinSupply_C|
|108|1C6686|P0110|DSM_CDKDfp_IATSCD_C|
|109|1C6688|P1612|DSM_CDKDfp_ImmCtlEep_C|
|110|1C668A|P0001|DSM_CDKDfp_InjCrv_InjLim_C|
|111|1C668C|P0200|DSM_CDKDfp_InjVlvBnk1A_C|
|112|1C668E|P0210|DSM_CDKDfp_InjVlvBnk1B_C|
|113|1C6690|P0211|DSM_CDKDfp_InjVlvBnk2A_C|
|114|1C6692|P0212|DSM_CDKDfp_InjVlvBnk2B_C|
|115|1C6694|P1169|DSM_CDKDfp_InjVlvChipA_C|
|116|1C6696|P1170|DSM_CDKDfp_InjVlvChipB_C|
|117|1C6698|P0201|DSM_CDKDfp_InjVlvCyl1A_C|
|118|1C669A|P0263|DSM_CDKDfp_InjVlvCyl1B_C|
|119|1C669C|P0202|DSM_CDKDfp_InjVlvCyl2A_C|
|120|1C669E|P0266|DSM_CDKDfp_InjVlvCyl2B_C|
|121|1C66A0|P0203|DSM_CDKDfp_InjVlvCyl3A_C|
|122|1C66A2|P0269|DSM_CDKDfp_InjVlvCyl3B_C|
|123|1C66A4|P0204|DSM_CDKDfp_InjVlvCyl4A_C|
|124|1C66A6|P0272|DSM_CDKDfp_InjVlvCyl4B_C|
|125|1C66A8|P0000|DSM_CDKDfp_InjVlvCyl5A_C|
|126|1C66AA|P0000|DSM_CDKDfp_InjVlvCyl5B_C|
|127|1C66AC|P0000|DSM_CDKDfp_InjVlvCyl6A_C|
|128|1C66AE|P0000|DSM_CDKDfp_InjVlvCyl6B_C|
|129|1C66B0|P0000|DSM_CDKDfp_MIL_C|
|130|1C66B2|P0215|DSM_CDKDfp_MRlyCD_C|
|131|1C66B4|P1210|DSM_CDKDfp_MeUnCDNoLoad_C|
|132|1C66B6|P1209|DSM_CDKDfp_MeUnCDSCBat_C|
|133|1C66B8|P1208|DSM_CDKDfp_MeUnCDSCGnd_C|
|134|1C66BA|P1207|DSM_CDKDfp_MeUnCD_ADC_C|
|135|1C66BC|P1700|DSM_CDKDfp_Montr_C|
|136|1C66BE|U1113|DSM_CDKDfp_NetMngABS_C|
|137|1C66C0|U1118|DSM_CDKDfp_NetMngBSI_C|
|138|1C66C2|U1109|DSM_CDKDfp_NetMngBVA_C|
|139|1C66C4|U1003|DSM_CDKDfp_NetMngCANBus_C|
|140|1C66C6|U1213|DSM_CDKDfp_NetMngCDS_C|
|141|1C66C8|U1000|DSM_CDKDfp_NetMngEDC_C|
|142|1C66CA|P0195|DSM_CDKDfp_OTSCD_C|
|143|1C66CC|P1631|DSM_CDKDfp_OvRMon_C|
|144|1C66CE|P1634|DSM_CDKDfp_OvRMonSigA_C|
|145|1C66D0|P1411|DSM_CDKDfp_OxiCCDTPre_C|
|146|1C66D2|P0247|DSM_CDKDfp_PCRGvnrDvtMax_C|
|147|1C66D4|P0248|DSM_CDKDfp_PCRGvnrDvtMin_C|
|148|1C66D6|P1416|DSM_CDKDfp_PFltCDTPre_C|
|149|1C66D8|P1429|DSM_CDKDfp_PFltCD_PresDiff_C|
|150|1C66DA|P1475|DSM_CDKDfp_PFltCD_PresDiffLong_C|
|151|1C66DC|P1457|DSM_CDKDfp_PFltChrgAbsnt_C|
|152|1C66DE|P1447|DSM_CDKDfp_PFltChrgMax_C|
|153|1C66E0|P0420|DSM_CDKDfp_PFltChrgOvr_C|
|154|1C66E2|P1448|DSM_CDKDfp_PFltDfl_C|
|155|1C66E4|P1639|DSM_CDKDfp_PSPCD_Actr_C|
|156|1C66E6|P0565|DSM_CDKDfp_PrpCCDKey_C|
|157|1C66E8|P0190|DSM_CDKDfp_RailCD_C|
|158|1C66EA|P1164|DSM_CDKDfp_RailCDOfsTst_C|
|159|1C66EC|P0230|DSM_CDKDfp_RailMeUn0_C|
|160|1C66EE|P0231|DSM_CDKDfp_RailMeUn1_C|
|161|1C66F0|P0232|DSM_CDKDfp_RailMeUn2_C|
|162|1C66F2|P1113|DSM_CDKDfp_RailMeUn3_C|
|163|1C66F4|P1166|DSM_CDKDfp_RailMeUn4_C|
|164|1C66F6|P0173|DSM_CDKDfp_RailMeUn7_C|
|165|1C66F8|P0000|DSM_CDKDfp_RailMeUn9_C|
|166|1C66FA|P1186|DSM_CDKDfp_RailMeUnFlEmp_C|
|167|1C66FC|P1186|DSM_CDKDfp_RailMeUnFlEmp0_C|
|168|1C66FE|P1632|DSM_CDKDfp_SOPTst_C|
|169|1C6700|P0608|DSM_CDKDfp_SSpMon1_C|
|170|1C6702|P0609|DSM_CDKDfp_SSpMon2_C|
|171|1C6704|P1710|DSM_CDKDfp_SSpMon3_C|
|172|1C6706|P1694|DSM_CDKDfp_StSys_C|
|173|1C6708|P1693|DSM_CDKDfp_StSysCAN_C|
|174|1C670A|P0615|DSM_CDKDfp_StrtCD_C|
|175|1C670C|P0000|DSM_CDKDfp_SysLamp_C|
|176|1C670E|P1511|DSM_CDKDfp_T15CD_C|
|177|1C6710|U2000|DSM_CDKDfp_T15CDMn_C|
|178|1C6712|U2118|DSM_CDKDfp_T15CDPart_C|
|179|1C6714|P1635|DSM_CDKDfp_TPUMon_C|
|180|1C6716|P0123|DSM_CDKDfp_TVACD_Max_C|
|181|1C6718|P0122|DSM_CDKDfp_TVACD_Min_C|
|182|1C671A|P0120|DSM_CDKDfp_TVACD_SigNpl_C|
|183|1C671C|P0121|DSM_CDKDfp_TVASCD_C|
|184|1C671E|P1464|DSM_CDKDfp_TVASCD_JamVlv_C|
|185|1C6720|P1463|DSM_CDKDfp_TVASCD_LgTimeDrft_C|
|186|1C6722|P1153|DSM_CDKDfp_TVASCD_ShTimeDrft_C|
|187|1C6724|P1161|DSM_CDKDfp_ThrVlvGvnrDvt_C|
|188|1C6726|P1155|DSM_CDKDfp_ThrVlvJamVlv_C|
|189|1C6728|P0000|DSM_CDKDfp_TtLpCD_C|
|190|1C672A|P1107|DSM_CDKDfp_VSACD_C|
|191|1C672C|P0501|DSM_CDKDfp_VSSCD1_C|
|192|1C672E|P1504|DSM_CDKDfp_VSSCD2_C|
|193|1C6730|P0500|DSM_CDKDfp_VSSCD3_C|
|194|1C6732|P1621|DSM_CDKDfp_WdCom_C|
