package encoder;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.jena.rdf.model.Model;
import java.util.List;

public interface IEncoder {
    List<Model> buildModel(String message) throws HL7Exception;
}
