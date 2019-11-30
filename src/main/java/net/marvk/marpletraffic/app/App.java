package net.marvk.marpletraffic.app;

public final class App {
    private App() {
        throw new AssertionError("No instances of main class " + App.class);
    }

    public static void main(final String[] args) {
        MarpleUi.main(args);
    }
}
