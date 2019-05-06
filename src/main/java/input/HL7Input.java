package input;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class HL7Input extends Input {

    public HL7Input(String outputTopicName) {
        super(outputTopicName);
    }

    @Override
    public String getNextMessage() {
        LocalDateTime ldt = LocalDateTime.now();
        String localtimeString = DateTimeFormatter.ofPattern("yyyyMMddhhmmss", Locale.ENGLISH).format(ldt);
        int randomSBP = ThreadLocalRandom.current().nextInt(80, 200 + 1);
        int randomDBP = ThreadLocalRandom.current().nextInt(40, 130 + 1);
        int randomMBP = ThreadLocalRandom.current().nextInt(80, 110 + 1);
        int bodyTemp = ThreadLocalRandom.current().nextInt(33, 40 + 1);
        int pulse = ThreadLocalRandom.current().nextInt(50, 180 + 1);
        int bloodO2 = ThreadLocalRandom.current().nextInt(85, 99 + 1);

        String msg1 =
                "MSH|^~\\&|VSM002|MIRTH_CONNECT|HIS001|MIRTH_CONNECT|" + localtimeString + "||ORU^R01|MSG0000002|P|2.5|||NE|NE|CO|8859/1|ES-CO\r\n"
                + "PID||87345125|87345125^^^^CC||NATHALIA^ORTEGA||19821029|F\r\n"
                + "OBR|1||VS12350000|28562-7^Vital Signs^LN\r\n"
                + "OBX|1|NM|271649006^Systolic blood pressure^SNOMED-CT||" + randomSBP + "|mm[Hg]|90-120|N|||F|||" + localtimeString + "\r\n"
                + "OBX|2|NM|271650006^Diastolic blood pressure^SNOMED-CT||" + randomDBP + "|mm[Hg]|60-80|N|||F|||" + localtimeString + "\r\n"
                + "OBX|3|NM|6797001^Mean blood pressure^SNOMED-CT||" + randomMBP + "|mm[Hg]|92-96|N|||F|||" + localtimeString + "\r\n"
                + "OBX|4|NM|386725007^Body temperature^SNOMED-CT||" + bodyTemp + "|C|37|N|||F|||" + localtimeString + "\r\n"
                + "OBX|5|NM|78564009^Pulse rate^SNOMED-CT||" + pulse + "|bpm|60-100|N|||F|||" + localtimeString + "\r\n"
                + "OBX|6|NM|431314004^SpO2^SNOMED-CT||" + bloodO2 + "|%|94-100|N|||F|||" + localtimeString + "";

        String msg2 =
                  "MSH|^~\\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A01|MSG00001-|P|2.5.1\r\n"
                + "EVN|A01|198808181123\r\n"
                + "PID|||PATID1231^5^M11||JONES^WILLIAM^A^III||19610615|M-||C|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(91-9)379-1212|(919)271-3434||S||PATID12345001^2^M10|123456789|9-87654^NC\r\n"
                + "PV1|1|I|2000^2012^01||||004777^LEBAUER^SIDNEY^J.|||SUR||-||ADM|A0-\r\n"
                + "AL1|||^Cat dander|Respiratory distress\r\n"
                + "OBX|1|NM|GLU^Glucose Lvl|59|mg/dL|65-99^65^99|L|||F|||20150102000000|\r\n"
                + "DG1|1||78900^ABDMNAL PAIN UNSPCF SITE^I9CDX|||W\r\n"
                + "DG1|3||1488000^Postoperative nausea and vomiting^SCT|||W ";

        String msg3 =
                  "MSH|^~\\&|VSM001|MIRTH_CONNECT|HIS001|MIRTH_CONNECT|20100511220525||ORU^R01|MSG0000001|P|2.5|||NE|NE|CO|8859/1|ES-CO\r\n"
                + "PID||6537077|6537077^^^^CC||ANDRES FELIPE^FERNANDEZ CORTES||19860705|M\r\n"
                + "OBR|1||VS12340000|28562-7^Vital Signs^LN\r\n"
                + "OBX|1|NM|271649006^Systolic blood pressure^SNOMED-CT||132|mm[Hg]|90-120|H|||F|||20100511220525\r\n"
                + "OBX|2|NM|271650006^Diastolic blood pressure^SNOMED-CT||86|mm[Hg]|60-80|H|||F|||20100511220525\r\n"
                + "OBX|3|NM|6797001^Mean blood pressure^SNOMED-CT||94|mm[Hg]|92-96|N|||F|||20100511220525\r\n"
                + "OBX|4|NM|386725007^Body temperature^SNOMED-CT||37|C|37|N|||F|||20100511220525\r\n"
                + "OBX|5|NM|78564009^Pulse rate^SNOMED-CT||80|bpm|60-100|N|||F|||20100511220525\r\n"
                + "OBX|6|NM|431314004^SpO2^SNOMED-CT||90|%|94-100|L|||F|||20100511220525\r\n";

        String msg4 =
                  "MSH|^~\\&|VSM002|MIRTH_CONNECT|HIS001|MIRTH_CONNECT|20100511220625||ORU^R01|MSG0000002|P|2.5|||NE|NE|CO|8859/1|ES-CO\r\n"
                + "PID||87345125|87345125^^^^CC||NATHALIA^ORTEGA||19821029|F\r\n"
                + "OBR|1||VS12350000|28562-7^Vital Signs^LN\r\n"
                + "OBX|1|NM|271649006^Systolic blood pressure^SNOMED-CT||95|mm[Hg]|90-120|N|||F|||20100511220625\r\n"
                + "OBX|2|NM|271650006^Diastolic blood pressure^SNOMED-CT||70|mm[Hg]|60-80|N|||F|||20100511220625\r\n"
                + "OBX|3|NM|6797001^Mean blood pressure^SNOMED-CT||95|mm[Hg]|92-96|N|||F|||20100511220625\r\n"
                + "OBX|4|NM|386725007^Body temperature^SNOMED-CT||37|C|37|N|||F|||20100511220625\r\n"
                + "OBX|5|NM|78564009^Pulse rate^SNOMED-CT||80|bpm|60-100|N|||F|||20100511220625\r\n"
                + "OBX|6|NM|431314004^SpO2^SNOMED-CT||98|%|94-100|N|||F|||20100511220625\r\n";

        String msg5 =
                  "MSH|^~\\&|VSM002|MIRTH_CONNECT|HIS001|MIRTH_CONNECT|20100511220725||ORU^R01|MSG0000003|P|2.5|||NE|NE|CO|8859/1|ES-CO\r\n"
                + "PID||94672543|94672543^^^^CC||ALEXANDRA^ROJAS RINCON||19890507|F\r\n"
                + "OBR|1||VS12360000|28562-7^Vital Signs^LN\r\n"
                + "OBX|1|NM|271649006^Systolic blood pressure^SNOMED-CT||100|mm[Hg]|90-120|N|||F|||20100511220725\r\n"
                + "OBX|2|NM|271650006^Diastolic blood pressure^SNOMED-CT||68|mm[Hg]|60-80|N|||F|||20100511220725\r\n"
                + "OBX|3|NM|6797001^Mean blood pressure^SNOMED-CT||92|mm[Hg]|92-96|N|||F|||20100511220725\r\n"
                + "OBX|4|NM|386725007^Body temperature^SNOMED-CT||37|C|37|N|||F|||20100511220725\r\n"
                + "OBX|5|NM|78564009^Pulse rate^SNOMED-CT||120|bpm|60-100|H|||F|||20100511220725\r\n"
                + "OBX|6|NM|431314004^SpO2^SNOMED-CT||98|%|94-100|N|||F|||20100511220725\r\n";

        return msg5;
    }
}