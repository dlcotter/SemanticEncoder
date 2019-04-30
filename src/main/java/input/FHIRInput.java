package input;

public class FHIRInput extends Input {
    public FHIRInput() {
        super("FHIR");
    }

    @Override
    public String getNextMessage() {
        return null;
    }
}