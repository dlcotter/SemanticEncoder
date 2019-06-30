package input;

import domain.Patient;

import java.util.Stack;

public class PipeDelimitedPatientsInput extends Input {
    Stack<Patient> samplePatients = new Stack<>();

    public PipeDelimitedPatientsInput(String outputTopicName) {
        super(outputTopicName);

        // Initialize sample patients list (the cast of NYPD Blue, all in the hospital at once, sadly)
        samplePatients.add(new Patient() {{ identifier = "0123456789"; name = "Franz, Dennis"        ;birthDate = "10/28/1944" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "1234567890"; name = "Clapp, Gordon"        ;birthDate = "09/24/1948" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "2345678901"; name = "McDaniel, James"      ;birthDate = "03/25/1958" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "3456789012"; name = "Brochtrup, Bill"      ;birthDate = "03/07/1963" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "4567890123"; name = "Turturro, Nicholas"   ;birthDate = "01/29/1962" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "5678901234"; name = "Delaney, Kim"         ;birthDate = "11/29/1961" ;gender = "female" ;}});
        samplePatients.add(new Patient() {{ identifier = "6789012345"; name = "Simmons, Henry"       ;birthDate = "07/01/1970" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "7890123456"; name = "Lawrence, Sharon"     ;birthDate = "06/29/1961" ;gender = "female" ;}});
        samplePatients.add(new Patient() {{ identifier = "8901234567"; name = "Smits, Jimmy"         ;birthDate = "07/09/1955" ;gender = "male"   ;}});
        samplePatients.add(new Patient() {{ identifier = "9012345678"; name = "Gosselaar, Mark-Paul" ;birthDate = "03/01/1974" ;gender = "male"   ;}});
    }

    @Override
    public String getNextMessage() {
        if (samplePatients.empty())
            return "";

        String message = "";
        try {
            Patient patient = samplePatients.pop();
            message = patient.identifier + "|" + patient.name + "|" + patient.birthDate + "|" + patient.gender;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
}