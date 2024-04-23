<?php
// Obtener los datos recibidos en el cuerpo de la solicitud POST
$inputData = file_get_contents('php://input');

// Decodificar los datos JSON recibidos en un array asociativo
$data = json_decode($inputData, true);

// Verificar si se recibieron los datos del formulario de inicio de sesión
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($data['username']) && isset($data['password'])) {
    // Conectar a la base de datos (cambiar estos valores según tu configuración)
    $hostname = "db";
    $username = "admin";
    $password = "test";
    $db = "database";

    $conn = new mysqli($hostname, $username, $password, $db);

    // Verificar la conexión
    if ($conn->connect_error) {
        die("Error de conexión: " . $conn->connect_error);
    }

    // Obtener los datos del formulario
    $username = $data['username'];
    $password = $data['password'];

    // Consulta SQL para verificar las credenciales del usuario
    $query = "SELECT * FROM usuarios WHERE username = '$username' AND password = '$password'";
    $result = $conn->query($query);

    if ($result->num_rows > 0) {
        // El usuario existe y las credenciales son válidas
        $response["success"] = true;
        $response["message"] = "Login successful";
    } else {
        // Las credenciales son inválidas
        $response["success"] = false;
        $response["message"] = "Invalid username or password";
    }

    // Devolver la respuesta como JSON
    header('Content-Type: application/json');
    echo json_encode($response);

    // Cerrar la conexión a la base de datos
    $conn->close();
} else {
    // Si no se recibieron los datos del formulario, devolver un mensaje de error
    $response["success"] = false;
    $response["message"] = "No data received";

    // Devolver la respuesta como JSON
    header('Content-Type: application/json');
    echo json_encode($response);
}
?>
