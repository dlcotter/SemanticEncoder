package applications;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import common.ActiveMQEnabled;

import java.util.ArrayList;
import java.util.List;

public class EsperListener extends ActiveMQEnabled {
    EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider();

    public EsperListener(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);

        engine.getEPAdministrator().getConfiguration().addEventType(EsperHighBloodPressureEvent.class);

        String epl = "select bloodPressureValue from EsperHighBloodPressureEvent";
        EPStatement statement = engine.getEPAdministrator().createEPL(epl);
        statement.addListener( (newData, oldData) -> {
            int bp = (int) newData[0].get("bloodPressureValue");
            System.out.println("High blood pressure found:" + bp);
        });
    }

    @Override
    protected List<String> processInputText(String inputMessageText) {
//        System.out.println(inputMessageText);

        return new ArrayList<>();
    }
}
