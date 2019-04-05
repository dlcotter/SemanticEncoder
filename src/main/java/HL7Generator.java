import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class HL7Generator
{
    private ArrayList<String> messages = new ArrayList<>();

    public HL7Generator() {
        LocalDateTime ldt = LocalDateTime.now();
        String localtimeString = DateTimeFormatter.ofPattern("yyyyMMddhhmmss", Locale.ENGLISH).format(ldt);

        int randomSBP = ThreadLocalRandom.current().nextInt(80, 200 + 1);
        int randomDBP = ThreadLocalRandom.current().nextInt(40, 130 + 1);
        int randomMBP = ThreadLocalRandom.current().nextInt(80, 110 + 1);
        int bodyTemp = ThreadLocalRandom.current().nextInt(33, 40 + 1);
        int pulse = ThreadLocalRandom.current().nextInt(50, 180 + 1);
        int bloodO2 = ThreadLocalRandom.current().nextInt(85, 99 + 1);

        messages.add(
            "MSH|^~\\&|VSM002|MIRTH_CONNECT|HIS001|MIRTH_CONNECT|" + localtimeString + "||ORU^R01|MSG0000002|P|2.5|||NE|NE|CO|8859/1|ES-CO\r\n" +
            "PID||87345125|87345125^^^^CC||NATHALIA^ORTEGA||19821029|F\r\n" +
            "OBR|1||VS12350000|28562-7^Vital Signs^LN\r\n" +
            "OBX|1|NM|271649006^Systolic blood pressure^SNOMED-CT||" + randomSBP + "|mm[Hg]|90-120|N|||F|||" + localtimeString + "\r\n" +
            "OBX|2|NM|271650006^Diastolic blood pressure^SNOMED-CT||" + randomDBP + "|mm[Hg]|60-80|N|||F|||" + localtimeString + "\r\n" +
            "OBX|3|NM|6797001^Mean blood pressure^SNOMED-CT||" + randomMBP + "|mm[Hg]|92-96|N|||F|||" + localtimeString + "\r\n" +
            "OBX|4|NM|386725007^Body temperature^SNOMED-CT||" + bodyTemp + "|C|37|N|||F|||" + localtimeString + "\r\n" +
            "OBX|5|NM|78564009^Pulse rate^SNOMED-CT||" + pulse + "|bpm|60-100|N|||F|||" + localtimeString + "\r\n" +
            "OBX|6|NM|431314004^SpO2^SNOMED-CT||" + bloodO2 + "|%|94-100|N|||F|||" + localtimeString + "");

        messages.add(
            "MSH|^~\\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A01|MSG00001-|P|2.5.1\r\n"
            + "EVN|A01|198808181123\r\n"
            + "PID|||PATID1231^5^M11||JONES^WILLIAM^A^III||19610615|M-||C|1200 N ELM STREET^^GREENSBORO^NC^27401-1020|GL|(91-9)379-1212|(919)271-3434||S||PATID12345001^2^M10|123456789|9-87654^NC\r\n"
            + "NK1|1|JONES^BARBARA^K|WIFE||||||NK\r\n"
            + "PV1|1|I|2000^2012^01||||004777^LEBAUER^SIDNEY^J.|||SUR||-||ADM|A0-\r\n"
            + "AL1|||^Cat dander|Respiratory distress\r\n"
            + "ORC|NW|987654321^EPC|123456789^EPC||||||20161003000000|||SMITH\r\n"
            + "OBR|1|341856649^HNAM_ORDERID|000000000000000000|648088^Basic Metabolic Panel|||20150101000000|||||||||1620^Johnson^Corey^A||||||20150101000000|||F|||||||||||20150101000000|\r\n"
            + "OBX|1|NM|GLU^Glucose Lvl|59|mg/dL|65-99^65^99|L|||F|||20150102000000|\r\n"
            + "DG1|1||78900^ABDMNAL PAIN UNSPCF SITE^I9CDX|||W\r\n"
            + "DG1|3||1488000^Postoperative nausea and vomiting^SCT|||W\r\n");

        messages.add(
            "MSH|^~\\&|REGADT|GOOD HEALTH HOSPITAL|GHH LAB||200712311501||ADT^A04^ADT_A01|000001|P|2.6|||\r\n"
            + "EVN|A04|200701101500|200701101400|01||200701101410\r\n"
            + "PID|||191919^^^GOOD HEALTH HOSPITAL^MR^GOOD HEALTH HOSPITAL^^^USSSA^SS|253763|EVERYMAN^ADAM^A||19560129|M|||2222 HOME STREET^^ISHPEMING^MI^49849^\"\"^||555-555-2004|555-555- 2004||S|CHR|10199925^^^GOOD HEALTH HOSPITAL^AN|371-66-9256||\r\n"
            + "NK1|1|NUCLEAR^NELDA|SPO|6666 HOME STREET^^ISHPEMING^MI^49849^\"\"^|555-555-5001|555-555-5001~555-555-5001|C^FIRST EMERGENCY CONTACT\r\n"
            + "NK1|2|MUM^MARTHA|MTH|4444 HOME STREET^^ISHPEMING^MI^49849^\"\"^|555-555 2006|555-555-2006~555-555-2006|C^SECOND EMERGENCY CONTACT\r\n"
            + "NK1|3\r\n"
            + "NK1|4|||6666 WORKER LOOP^^ISHPEMING^MI^49849^\"\"^||(900)545-1200|E^EMPLOYER|19940605||PROGRAMMER|||WORK IS FUN, INC.\r\n"
            + "PV1||O|O/R||||0148^ATTEND^AARON^A|0148^ATTEND^AARON^A|0148^ATTEND^AARON^A|MED|||||||0148^ATTEND^AARON^A|S|1400|A||||||||||||||||||||||||199501101410|\r\n"
            + "PV2||||||||200701101400||||||||||||||||||||||||||N\r\n"
            + "OBX||ST|1010.1^BODY WEIGHT||62|kg|||||F\r\n" + "OBX||ST|1010.1^HEIGHT||190|cm|||||F\r\n"
            + "DG1|1|19||BIOPSY||A|\r\n"
            + "GT1|1||EVERYMAN^ADAM^A||2222 HOME STREET^^ISHPEMING^MI^49849^\"\"^|444-33 3333|555-555-2004||||SEL^SELF|444-33 3333||||AUTO CLINIC|2222 HOME STREET^^ISHPEMING^MI^49849^\"\"|555-555-2004|\r\n"
            + "IN1|0|0|UA1|UARE INSURED, INC.|8888 INSURERS CIRCLE^^ISHPEMING^M149849^\"\"^||555-555-3015|90||||||50 OK|\r\n"
            + "DG1|1||236084000^Chemotherapy-induced nausea and vomiting^SCT|||W\r\n");

        messages.add(
                "MSH|^~\\&|REGADT|MCM|IFENG||199112311501||ADT^A04^ADT_A01|000001|P|2.5|||\r\n"
                + "EVN|A04|200301101500|200301101400|01||200301101410\r\n"
                + "PID|||191919^^^GENHOS^MR~371-66-9256^^^USSSA^SS|253763|MASSIE^JAMES^A||19560129|M|||171 ZOBER-LEIN^^ISHPEMING^MI^49849^\"\"^||(900)485-5344|(900)485-5344||S|C|10199925^^^GENHOS^AN|371-66-9256||\r\n"
                + "NK1|1|MASSIE^ELLEN|SPOUSE|171 ZOBERLEIN^^ISHPEMING^MI^49849^\"\"^|(900)485-5344|(900)545-1234~(900)545-1200|EC1^FIRST EMERGENCY CONTACT\r\n"
                + "NK1|2|MASSIE^MARYLOU|MOTHER|300 ZOBERLEIN^^ISHPEMING^MI^49849^\"\"^|(900)485-5344|(900)545-1234~(900)545-1200|EC2^SECOND EMERGENCY CONTACT\r\n"
                + "NK1|3\r\n"
                + "NK1|4|||123 INDUSTRY WAY^^ISHPEMING^MI^49849^\"\"^||(900)545-1200|EM^EMPLOYER|19940605||PROGRAMMER|||ACME SOFTWARE COMPANY\r\n"
                + "PV1||O|O/R||||0148^ADDISON,JAMES|0148^ADDISON,JAMES|0148^ADDISON,JAMES|AMB|||||||0148^ADDISON,JAMES|S|1400|A|||||||||||||||||||GENHOS|||||199501101410|\r\n"
                + "PV2||||||||200301101400||||||||||||||||||||||||||200301101400\r\n"
                + "OBX||ST|1010.1^BODY WEIGHT||62|kg|||||F\r\n"
                + "OBX||ST|1010.1^HEIGHT||190|cm|||||F\r\n"
                + "DG1|1|19||BIOPSY||00|\r\n"
                + "GT1|1||MASSIE^JAMES^\"\"^\"\"^\"\"^\"\"^||171 ZOBERLEIN^^ISHPEMING^MI^49849^\"\"^|(900)485-5344|(900)485-5344||||SE^SELF|371-66-925||||MOOSES AUTO CLINIC|171 ZOBER-LEIN^^ISHPEMING^MI^49849^\"\"|(900)485-5344|\r\n"
                + "IN1|0|0|BC1|BLUE CROSS|171 ZOBERLEIN^^ISHPEMING^M149849^\"\"^||(900)485-5344|90||||||50 OK|\r\n"
                + "IN1|2|\"\"|\"\"");
    }

    public String getRandomMessage() {
        int n = new Random().nextInt(messages.size());
        return messages.get(n);
    }
}
