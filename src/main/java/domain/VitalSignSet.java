package domain;

import input.HL7VitalSignsInput;

public class VitalSignSet {
    public VitalSign systolicBloodPressure, diastolicBloodPressure, meanBloodPressure, bodyTemperature, pulseRate, SPO2;

    public VitalSignSet(HL7VitalSignsInput.SimulationMode simulationMode) {
        switch (simulationMode) {
            case NORMAL:
                this.systolicBloodPressure = new VitalSign(VitalSignType.SYSTOLIC_BLOOD_PRESSURE, VitalSignRange.NORMAL);
                this.diastolicBloodPressure = new VitalSign(VitalSignType.DIASTOLIC_BLOOD_PRESSURE, VitalSignRange.NORMAL);
                this.meanBloodPressure = new VitalSign(VitalSignType.MEAN_BLOOD_PRESSURE, VitalSignRange.NORMAL);
                this.bodyTemperature = new VitalSign(VitalSignType.BODY_TEMPERATURE, VitalSignRange.NORMAL);
                this.pulseRate = new VitalSign(VitalSignType.PULSE_RATE, VitalSignRange.NORMAL);
                this.SPO2 = new VitalSign(VitalSignType.SPO2, VitalSignRange.NORMAL);
                break;

            case HYPOTENSION:
                this.systolicBloodPressure = new VitalSign(VitalSignType.SYSTOLIC_BLOOD_PRESSURE, VitalSignRange.LOW);
                this.diastolicBloodPressure = new VitalSign(VitalSignType.DIASTOLIC_BLOOD_PRESSURE, VitalSignRange.LOW);
                this.meanBloodPressure = new VitalSign(VitalSignType.MEAN_BLOOD_PRESSURE, VitalSignRange.LOW);
                this.bodyTemperature = new VitalSign(VitalSignType.BODY_TEMPERATURE, Math.random() > 0.5 ? VitalSignRange.LOW : VitalSignRange.NORMAL);
                this.pulseRate = new VitalSign(VitalSignType.PULSE_RATE, VitalSignRange.HIGH);
                this.SPO2 = new VitalSign(VitalSignType.SPO2, VitalSignRange.LOW);
                break;

            case HYPOTHERMIA:
                this.systolicBloodPressure = new VitalSign(VitalSignType.SYSTOLIC_BLOOD_PRESSURE, VitalSignRange.HIGH);
                this.diastolicBloodPressure = new VitalSign(VitalSignType.DIASTOLIC_BLOOD_PRESSURE, VitalSignRange.HIGH);
                this.meanBloodPressure = new VitalSign(VitalSignType.MEAN_BLOOD_PRESSURE, VitalSignRange.HIGH);
                this.bodyTemperature = new VitalSign(VitalSignType.BODY_TEMPERATURE, VitalSignRange.LOW);
                this.pulseRate = new VitalSign(VitalSignType.PULSE_RATE, VitalSignRange.HIGH);
                this.SPO2 = new VitalSign(VitalSignType.SPO2, VitalSignRange.NORMAL);
                break;
        }
    }

    public VitalSign getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public VitalSign getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    public VitalSign getMeanBloodPressure() {
        return meanBloodPressure;
    }

    public VitalSign getBodyTemperature() {
        return bodyTemperature;
    }

    public VitalSign getPulseRate() {
        return pulseRate;
    }

    public VitalSign getSPO2() {
        return SPO2;
    }
}
