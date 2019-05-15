package encoder;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.parser.DefaultEscaping;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.Escaping;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HL7EncoderGeneric extends Encoder implements IEncoder {
    // Adapted from code at https://github.com/housseindh/Hl7ToRDF

    private static final Escaping HL7_ESCAPING = new DefaultEscaping();
    private static final String HL7_URI = "http://www.HL7.org/segment#";
    private static final Pattern p = Pattern.compile("^(cm_msg|[a-z][a-z][a-z]?)([0-9]+)_(\\w+)$");
    private HapiContext hapiContext = new DefaultHapiContext();
    private Model model = ModelFactory.createDefaultModel();
    private Resource resource;

    public HL7EncoderGeneric(String inputTopicName, String outputTopicName) {
        super(inputTopicName, outputTopicName);
        hapiContext.setValidationContext((ValidationContext) ValidationContextFactory.noValidation());
    }

    @Override
    public List<Model> buildModel(String message) {
        PipeParser parser = hapiContext.getPipeParser();

        try {
            Message group = parser.parse(message);
            String uuid = this.findPID(group); // not sure what to do about the exception this line's getting
            resource = model.getResource(HL7_URI + uuid);
            createSegments(group);
        } catch (HL7Exception e) {
            e.printStackTrace();
        }

        List<Model> models = new ArrayList<>();
        models.add(model);

        return models;
    }

    private String findPID(final Group group) throws HL7Exception {
        try {
            Structure[] segments = group.getAll("PID");
            if (segments.length == 0)
                return null;

            Segment pid = (Segment)segments[0];
            Map<String, Type> segmentPid = createFields(pid);
            Map<String, Type> componentId = createComponents(segmentPid.get("PatientIdentifierList"));

            return componentId.get("IDNumber").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notEmpty(group)) {
            for (final String name : group.getNames()) {
                for (final Structure structure : group.getAll(name)) {
                    if (group.isGroup(name) && structure instanceof Group) {
                        String id = findPID((Group)structure);
                        if (!id.equals(""))
                            return id;
                    }
                }
            }
        }
        return "";
    }

    private void createSegments(final Group group) throws HL7Exception {
        if (notEmpty(group)) {
            for (final String name : group.getNames()) {
                for (final Structure structure : group.getAll(name)) {
                    if (group.isGroup(name) && structure instanceof Group) {
                        createSegments((Group) structure);
                    } else if (structure instanceof Segment) {
                        createSegment((Segment) structure);
                    }
                }
            }
        }
    }

    private void createSegment(final Segment segment) throws HL7Exception {
        if (notEmpty(segment)) {
            final String segmentKey = segment.getName();
            convertHL7ToRDF(model, segmentKey, segment);
        }
    }

    private Property createProperty(Model model, String s) {
        return model.createProperty(s);
    }

    private void convertHL7ToRDF(Model model, String segmentKey, Segment segment) throws HL7Exception {
        Resource segmentResource = model.createResource();
        resource.addProperty(createProperty(model, HL7_URI + segmentKey), segmentResource);

        //System.out.println("segmentKey:" + segmentKey );//+ " | segment:" + segment);

        final Map<String, Type> fields = createFields(segment);
        for (final Map.Entry<String, Type> fieldEntry : fields.entrySet()) {
            final String fieldKey = fieldEntry.getKey();
            final Type field = fieldEntry.getValue();
            Map<String,Type> mapComponents =  createComponents(field);

            if (mapComponents.size()== 0){
                segmentResource.addProperty(createProperty(model, HL7_URI + fieldKey), field.toString());
            } else {
                Resource componentResource = model.createResource();


                for (final Map.Entry<String, Type> componentEntry : mapComponents.entrySet()) {
                    final String componentKey = componentEntry.getKey();
                    final Type component = componentEntry.getValue();

                    final String componentValue = HL7_ESCAPING.unescape(component.encode(), EncodingCharacters.defaultInstance());

                    segmentResource.addProperty(createProperty(model, HL7_URI + fieldKey), componentResource
                            .addProperty(createProperty(model, HL7_URI + componentKey), componentValue));
                }
            }

        }
    }

    private Map<String, Type> createFields(final Segment segment) throws HL7Exception {
        final Map<String, Type> fields = new TreeMap<>();
        final String[] segmentNames = segment.getNames();
        for (int i = 1; i <= segment.numFields(); i++) {
            final Type field = segment.getField(i, 0);
            if (notEmpty(field)) {
                final String fieldName;
                fieldName = WordUtils.capitalize(segmentNames[i-1]).replaceAll("\\W+", "");
                fields.put(fieldName, field);
            }
        }
        return fields;
    }

    private Map<String, Type> createComponents(final Type field) throws HL7Exception {
        final Map<String, Type> components = new TreeMap<>();
        if (notEmpty(field) && (field instanceof Composite)) {
            try {
                final java.beans.PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(field);
                for (final java.beans.PropertyDescriptor property : properties) {
                    final String name = property.getName();
                    final Matcher matcher = p.matcher(name);
                    if (matcher.find()) {
                        final Type type = (Type) PropertyUtils.getProperty(field, name);
                        if (notEmpty(type)) {
                            final String componentName = matcher.group(3);

                            components.put(componentName, type);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }
        return components;
    }

    private boolean notEmpty(final Visitable vis) throws HL7Exception {
        return (vis != null && !vis.isEmpty());
    }
}
