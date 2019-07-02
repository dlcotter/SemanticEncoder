package domain;

import org.jetbrains.annotations.NotNull;

public class VitalSign {
    VitalSignType type;
    VitalSignRange range;
    String name, code;
    double minimumNormal, maximumNormal;

    public VitalSign(@NotNull VitalSignType type, VitalSignRange range) {
        this.type = type;
        this.range = range;

        switch (type) {
            case SYSTOLIC_BLOOD_PRESSURE:
                this.name = "Systolic blood pressure";
                this.code = "271649006";
                this.minimumNormal = 90;
                this.maximumNormal = 120;
                break;

            case DIASTOLIC_BLOOD_PRESSURE:
                this.name = "Diastolic blood pressure";
                this.code = "271650006";
                this.minimumNormal = 60;
                this.maximumNormal = 80;
                break;

            case MEAN_BLOOD_PRESSURE:
                this.name = "Mean blood pressure";
                this.code = "6797001";
                this.minimumNormal = 92;
                this.maximumNormal = 96;
                break;

            case BODY_TEMPERATURE:
                this.name = "Body temperature";
                this.code = "386725007";
                this.minimumNormal = 36.1;
                this.maximumNormal = 37.2;
                break;

            case PULSE_RATE:
                this.name = "Pulse rate";
                this.code = "78564009";
                this.minimumNormal = 60;
                this.maximumNormal = 100;
                break;

            case SPO2:
                this.name = "SpO2";
                this.code = "431314004";
                this.minimumNormal = 94;
                this.maximumNormal = 100;
                break;
        }
    }

    public VitalSignType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    double low() {
        return minimumNormal - (Math.random()*(maximumNormal - minimumNormal));
    }

    double normal() {
        return minimumNormal + (Math.random()*(maximumNormal - minimumNormal));
    }

    double high() {
        return maximumNormal + (Math.random()*(maximumNormal - minimumNormal));
    }

    public double getValue() {
        switch (range) {
            case LOW:
                return this.low();
            case HIGH:
                return this.high();
            case NORMAL:
                return this.normal();
        }

        return this.normal();
    }
}
