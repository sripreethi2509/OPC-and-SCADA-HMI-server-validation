import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketDataBroadcaster {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Fetch real-time values from OPC UA server
                double temperature = LifeSciencesOpcServer.getTemperatureValue();
                double pressure = LifeSciencesOpcServer.getPressureValue();
                double humidity = LifeSciencesOpcServer.getHumidityValue();

                // Create JSON-like message
                String message = String.format(
                        "{\"temperature\": %.2f, \"pressure\": %.2f, \"humidity\": %.2f}",
                        temperature, pressure, humidity
                );

                // Broadcast the data to all connected WebSocket clients
                ScadaWebSocketServer.broadcast(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
