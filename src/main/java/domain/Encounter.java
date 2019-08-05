package domain;

import java.util.ArrayList;
import java.util.List;

public class Encounter {
    public String identifier, bed, building, floor, patientIdentifier;

    public final static List<Encounter> getSampleEncounters() {
        List<Encounter> sampleEncounters = new ArrayList<>();

        sampleEncounters.add(new Encounter() {{ identifier = "000021883-2315"; patientIdentifier = "000021883"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000046879-4653"; patientIdentifier = "000046879"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000024343-8765"; patientIdentifier = "000024343"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000745190-0465"; patientIdentifier = "000745190"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000045656-5231"; patientIdentifier = "000045656"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000092113-5278"; patientIdentifier = "000092113"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000025634-9832"; patientIdentifier = "000025634"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000059831-0562"; patientIdentifier = "000059831"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000036564-9807"; patientIdentifier = "000036564"; }});
        sampleEncounters.add(new Encounter() {{ identifier = "000085762-5631"; patientIdentifier = "000085762"; }});

        return sampleEncounters;
    }
}
