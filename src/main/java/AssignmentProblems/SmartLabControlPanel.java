import java.util.*;
 
class InvalidValueException extends IllegalArgumentException {
    InvalidValueException(String message) { super(message); }
}
 
interface Capability {
    String getName();
    String apply(int value);      // returns a description of what happened; throws InvalidValueException
}
 
class PowerCapability implements Capability {
    static final String NAME = "Power";
    private boolean on;
 
    public String getName() { return NAME; }
 
    public String apply(int value) {
        if (value != 0 && value != 1) throw new InvalidValueException("power must be 0 (OFF) or 1 (ON)");
        on = (value == 1);
        return on ? "ON" : "OFF";
    }
 
    boolean isOn() { return on; }
}
 
abstract class RangeCapability implements Capability {
    private final String name, label, unit;
    private final int min, max;
    private int current;
 
    protected RangeCapability(String name, String label, int min, int max, String unit) {
        this.name = name;
        this.label = label;
        this.min = min;
        this.max = max;
        this.unit = unit;
        this.current = min;
    }
 
    public String getName() { return name; }
 
    public String apply(int value) {
        if (value < min || value > max) {          // validate first, mutate second
            throw new InvalidValueException(label + " must be between " + min + unit + " and " + max + unit);
        }
        current = value;
        return label + " set to " + value + unit;
    }
 
    int getCurrent() { return current; }
}
 
class BrightnessCapability extends RangeCapability {
    static final String NAME = "Brightness";
    BrightnessCapability() { super(NAME, "brightness", 0, 100, "%"); }
}
 
class TemperatureCapability extends RangeCapability {
    static final String NAME = "Temperature";
    TemperatureCapability() { super(NAME, "temperature", 16, 30, "\u00B0C"); }
}
 
class Device {
    private final String name;
    private final Map<String, Capability> capabilities = new LinkedHashMap<>();
 
    Device(String name) { this.name = name; }
 
    String getName() { return name; }
 
    String addCapability(Capability c) {
        if (capabilities.containsKey(c.getName())) {
            throw new IllegalStateException(name + " already has " + c.getName());
        }
        capabilities.put(c.getName(), c);
        return name + ": " + c.getName() + " capability added";
    }
 
    boolean hasCapability(String capabilityName) { return capabilities.containsKey(capabilityName); }
 
    String execute(String capabilityName, int value) {
        Capability c = capabilities.get(capabilityName);
        if (c == null) throw new IllegalStateException(name + " does not support " + capabilityName);
        try {
            return name + ": " + c.apply(value);
        } catch (InvalidValueException e) {
            throw new InvalidValueException(name + " " + e.getMessage());   // device state unchanged
        }
    }
}
 
class SceneStep {
    private final String capabilityName;
    private final int value;
 
    SceneStep(String capabilityName, int value) {
        this.capabilityName = capabilityName;
        this.value = value;
    }
 
    String getCapabilityName() { return capabilityName; }
    int getValue() { return value; }
}
 
class Scene {
    private final String name;
    private final List<SceneStep> steps = new ArrayList<>();
 
    Scene(String name) { this.name = name; }
 
    Scene addStep(String capabilityName, int value) {
        steps.add(new SceneStep(capabilityName, value));
        return this;
    }
 
    int run(List<Device> devices) {
        System.out.println("Scene '" + name + "' started.");
        int applied = 0;
        for (SceneStep step : steps) {
            for (Device d : devices) {
                if (!d.hasCapability(step.getCapabilityName())) continue;   // skip unsupported devices
                try {
                    System.out.println(d.execute(step.getCapabilityName(), step.getValue()) + ".");
                    applied++;
                } catch (InvalidValueException e) {
                    System.out.println("Rejected: " + e.getMessage() + ".");
                }
            }
        }
        System.out.println("Scene '" + name + "' completed: " + applied + " actions applied.");
        return applied;
    }
}
 
public class Main {
    public static void main(String[] args) {
        Device ac = new Device("Lab AC");
        ac.addCapability(new PowerCapability());
        ac.addCapability(new TemperatureCapability());
 
        Device lights = new Device("Ceiling Lights");
        lights.addCapability(new PowerCapability());
        lights.addCapability(new BrightnessCapability());
 
        Device projector = new Device("Projector");
        projector.addCapability(new PowerCapability());
 
        List<Device> devices = List.of(ac, lights, projector);
 
        Scene lectureMode = new Scene("Lecture Mode")
                .addStep(PowerCapability.NAME, 1)
                .addStep(BrightnessCapability.NAME, 40)
                .addStep(TemperatureCapability.NAME, 24);
        lectureMode.run(devices);
 
        try {
            System.out.println(ac.execute(TemperatureCapability.NAME, 12) + ".");
        } catch (InvalidValueException e) {
            System.out.println("Rejected: " + e.getMessage() + ".");
        }
 
        System.out.println(projector.addCapability(new BrightnessCapability()) + ".");
        System.out.println(projector.execute(BrightnessCapability.NAME, 70) + ".");
    }
}
