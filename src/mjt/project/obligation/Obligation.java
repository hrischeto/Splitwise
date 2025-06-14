package mjt.project.obligation;

public record Obligation(String receiver, double amount, String reason) {
    @Override
    public String toString() {
        return "You owe " + receiver + " " + amount + "LV [" + reason + " ].\n";
    }
}