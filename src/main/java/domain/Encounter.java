package domain;

import java.util.ArrayList;
import java.util.List;

public class Encounter {
    public String identifier, bed, building, floor, patientIdentifier;

    public static List<Encounter> getSampleEncounters() {
        List<Encounter> sampleEncounters = new ArrayList<>();

        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(0).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(1).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(2).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(3).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(4).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(5).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(6).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(7).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(8).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ patientIdentifier = Patient.getSamplePatients().get(9).identifier; identifier = this.patientIdentifier + "-" + Utils.randomNumericIdentifier(4); }});

        return sampleEncounters;
    }
}
