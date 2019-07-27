package domain;

import java.util.ArrayList;
import java.util.List;

public class Patient {
    public String identifier, name, birthDate, gender;

    public static List<Patient> getSamplePatients() {
        List<Patient> samplePatients = new ArrayList<>();

        // Initialize sample patients list (the cast of NYPD Blue, all in the hospital at once, sadly)
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Franz, Dennis"        ;birthDate = "10/28/1944" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Clapp, Gordon"        ;birthDate = "09/24/1948" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "McDaniel, James"      ;birthDate = "03/25/1958" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Brochtrup, Bill"      ;birthDate = "03/07/1963" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Turturro, Nicholas"   ;birthDate = "01/29/1962" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Delaney, Kim"         ;birthDate = "11/29/1961" ;gender = "female" ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Simmons, Henry"       ;birthDate = "07/01/1970" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Lawrence, Sharon"     ;birthDate = "06/29/1961" ;gender = "female" ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Smits, Jimmy"         ;birthDate = "07/09/1955" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = Utils.randomNumericIdentifier(9); name = "Gosselaar, Mark-Paul" ;birthDate = "03/01/1974" ;gender = "male"   ;}});

        return samplePatients;
    }
}
