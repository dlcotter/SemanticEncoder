package domain;

public class Observation {
    public String observationID, observationDateTime, encounterIdentifier;
    public CodedElement code;
    public CodedElement[] components;
    public Quantity[] quantities;
}

