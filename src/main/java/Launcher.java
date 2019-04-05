public class Launcher {
    public static void main(String[]  args) {
        // add HL7 producer p1
//        HL7Producer p1 = new HL7Producer();
//        p1.start();

        // add FHIR producer p2

        // add Semantic Encoder producer/consumer
//        SemanticEncoder semanticEncoder = new SemanticEncoder();
//        semanticEncoder.addHL7Consumer();

        // add CEP consumer c1

        // add OMOP consumer c2

        // send HL7 message from p1
        // pull message to semantic encoder
        // convert message from HL7 to RDF
        // pull CEP message to c1
        // pull message to c2

        HAPITester tst = new HAPITester();
        tst.GetVitalSigns();

    }
}
