package input;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class HL7Input extends Input {

    public HL7Input(String outputTopicName) {
        super(outputTopicName);
    }

    @Override
    public String getNextMessage() {
//        LocalDateTime ldt = LocalDateTime.now();
//        String localtimeString = DateTimeFormatter.ofPattern("yyyyMMddhhmmss", Locale.ENGLISH).format(ldt);
//        int randomSBP = ThreadLocalRandom.current().nextInt(80, 200 + 1);
//        int randomDBP = ThreadLocalRandom.current().nextInt(40, 130 + 1);
//        int randomMBP = ThreadLocalRandom.current().nextInt(80, 110 + 1);
//        int bodyTemp = ThreadLocalRandom.current().nextInt(33, 40 + 1);
//        int pulse = ThreadLocalRandom.current().nextInt(50, 180 + 1);
//        int bloodO2 = ThreadLocalRandom.current().nextInt(85, 99 + 1);

//          "MSH|^~\\&|VSM002|MIRTH_CONNECT|HIS001|MIRTH_CONNECT|" + localtimeString + "||ORU^R01|MSG0000002|P|2.5|||NE|NE|CO|8859/1|ES-CO\r\n"
//        + "PID||87345125|87345125^^^^CC||NATHALIA^ORTEGA||19821029|F\r\n"
//        + "OBR|1||VS12350000|28562-7^Vital Signs^LN\r\n"
//        + "OBX|1|NM|271649006^Systolic blood pressure^SNOMED-CT||" + randomSBP + "|mm[Hg]|90-120|N|||F|||" + localtimeString + "\r\n"
//        + "OBX|2|NM|271650006^Diastolic blood pressure^SNOMED-CT||" + randomDBP + "|mm[Hg]|60-80|N|||F|||" + localtimeString + "\r\n"
//        + "OBX|3|NM|6797001^Mean blood pressure^SNOMED-CT||" + randomMBP + "|mm[Hg]|92-96|N|||F|||" + localtimeString + "\r\n"
//        + "OBX|4|NM|386725007^Body temperature^SNOMED-CT||" + bodyTemp + "|C|37|N|||F|||" + localtimeString + "\r\n"
//        + "OBX|5|NM|78564009^Pulse rate^SNOMED-CT||" + pulse + "|bpm|60-100|N|||F|||" + localtimeString + "\r\n"
//        + "OBX|6|NM|431314004^SpO2^SNOMED-CT||" + bloodO2 + "|%|94-100|N|||F|||" + localtimeString + "";

        return    "MSH|^~\\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A01|MSG00001-|P|2.5.1\r\n"
                + "EVN|A01|198808181123\r\n"
                + "PID|||PATID1231^5^M11||JONES^WILLIAM^A^III||19610615|M-||C|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(91-9)379-1212|(919)271-3434||S||PATID12345001^2^M10|123456789|9-87654^NC\r\n"
                + "PV1|1|I|2000^2012^01||||004777^LEBAUER^SIDNEY^J.|||SUR||-||ADM|A0-\r\n"
                + "AL1|||^Cat dander|Respiratory distress\r\n"
                + "OBX|1|NM|GLU^Glucose Lvl|59|mg/dL|65-99^65^99|L|||F|||20150102000000|\r\n"
                + "DG1|1||78900^ABDMNAL PAIN UNSPCF SITE^I9CDX|||W\r\n"
                + "DG1|3||1488000^Postoperative nausea and vomiting^SCT|||W ";
    }
}