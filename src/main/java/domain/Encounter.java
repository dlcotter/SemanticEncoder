package domain;

import java.util.ArrayList;
import java.util.List;

public class Encounter {
    public String identifier, bed, building, floor;

    public static List<Encounter> getSampleEncounters() {
        List<Encounter> sampleEncounters = new ArrayList<>();

        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(0).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(1).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(2).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(3).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(4).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(5).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(6).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(7).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(8).identifier + "-" + Utils.randomNumericIdentifier(4); }});
        sampleEncounters.add(new Encounter() {{ identifier = Patient.getSamplePatients().get(9).identifier + "-" + Utils.randomNumericIdentifier(4); }});

        return sampleEncounters;
    }
}
