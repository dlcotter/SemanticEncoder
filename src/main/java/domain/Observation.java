package domain;

public class Observation {
    public String identifier, observationDateTime, patientIdentifier;
    public CodedElement code;
    public CodedElement[] components;
    public Quantity[] quantities;
}

