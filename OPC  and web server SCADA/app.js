// Initialize WebSocket connection
const socket = new WebSocket('ws://localhost:8080/data');

// Event listener when WebSocket is connected
socket.onopen = function() {
    console.log('WebSocket connection established.');
};

// Event listener for receiving messages (real-time data)
socket.onmessage = function(event) {
    const data = JSON.parse(event.data);

    // Update the SCADA UI with real-time values
    $('#temperature').text(data.temperature.toFixed(2));
    $('#pressure').text(data.pressure.toFixed(2));
    $('#humidity').text(data.humidity.toFixed(2));
};

// Event listener for WebSocket errors
socket.onerror = function(error) {
    console.error('WebSocket Error: ', error);
};

// Event listener when WebSocket is closed
socket.onclose = function() {
    console.log('WebSocket connection closed.');
};

// Send commands from the control form to the WebSocket server
$('#control-form').submit(function(event) {
    event.preventDefault();  // Prevent form from submitting the traditional way

    // Capture input values
    const setTemperature = $('#setTemperature').val();
    const setPressure = $('#setPressure').val();
    const setHumidity = $('#setHumidity').val();

    // Create a command object
    const command = {
        setTemperature: setTemperature,
        setPressure: setPressure,
        setHumidity: setHumidity
    };

    // Send the command object to the WebSocket server
    socket.send(JSON.stringify(command));

    // Clear the form fields after submission
    $('#control-form')[0].reset();
});
