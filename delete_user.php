<?php
// Obtener los datos recibidos en el cuerpo de la solicitud POST
$inputData = file_get_contents('php://input');

// Decodificar los datos JSON
$requestData = json_decode($inputData, true);

// Verificar si se recibieron los datos del formulario
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($requestData['username'])) {
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

    // Mostrar mensaje en consola
    error_log("Conexión a la base de datos establecida");

    // Obtener el nombre de usuario a eliminar
    $usernameToDelete = $requestData['username'];

    // Consulta SQL para eliminar el usuario
    $query = "DELETE FROM usuarios WHERE username = '$usernameToDelete'";
    if ($conn->query($query) === TRUE) {
        // Eliminación exitosa
        $response["success"] = true;
        $response["message"] = "User deleted successfully";
    } else {
        // Error en la eliminación
        $response["success"] = false;
        $response["message"] = "Error deleting user: " . $conn->error; // Mostrar el mensaje de error de MySQL
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
