package domain;

import java.util.ArrayList;
import java.util.List;

public class Patient {
    public String identifier, name, birthDate, gender;

    public final static List<Patient> getSamplePatients() {
        List<Patient> samplePatients = new ArrayList<>();

        // Initialize sample patients list (the cast of NYPD Blue, all in the hospital at once, sadly)
        samplePatients.add(new Patient() {{ identifier = "000021883"; name = "Franz, Dennis"        ;birthDate = "1944-10-28T00:00:00" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "000046879"; name = "Clapp, Gordon"        ;birthDate = "1948-09-24T00:00:00" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "000024343"; name = "McDaniel, James"      ;birthDate = "1958-03-25T00:00:00" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "000745190"; name = "Brochtrup, Bill"      ;birthDate = "1963-03-07T00:00:00" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "000045656"; name = "Turturro, Nicholas"   ;birthDate = "1962-01-29T00:00:00" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "000092113"; name = "Delaney, Kim"         ;birthDate = "1961-11-29T00:00:00" ;gender = "female" ;}});
        samplePatients.add(new Patient() {{ identifier = "000025634"; name = "Simmons, Henry"       ;birthDate = "1970-07-01T00:00:00" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "000059831"; name = "Lawrence, Sharon"     ;birthDate = "1961-06-29T00:00:00" ;gender = "female" ;}});
        samplePatients.add(new Patient() {{ identifier = "000036564"; name = "Smits, Jimmy"         ;birthDate = "1955-07-09T00:00:00" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "000085762"; name = "Gosselaar, Mark-Paul" ;birthDate = "1974-03-01T00:00:00" ;gender = "male"   ;}});

        return samplePatients;
    }
}
