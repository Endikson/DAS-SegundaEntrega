<?php
// Obtener los datos recibidos en el cuerpo de la solicitud POST
$inputData = file_get_contents('php://input');

// Decodificar los datos JSON
$requestData = json_decode($inputData, true);

// Verificar si se recibieron los datos del formulario
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($requestData['email']) && isset($requestData['username']) && isset($requestData['password'])) {
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

    // Obtener los datos del formulario
    $email = $requestData['email'];
    $username = $requestData['username'];
    $password = $requestData['password'];

    // Consulta SQL para verificar si el email ya está registrado
    $query = "SELECT * FROM usuarios WHERE email = '$email'";
    $result = $conn->query($query);
    if ($result->num_rows > 0) {
        // El email ya está registrado
        $response["success"] = false;
        $response["message"] = "Email already exists";
    } else {
        // Consulta SQL para verificar si el usuario ya está registrado
        $query = "SELECT * FROM usuarios WHERE username = '$username'";
        $result = $conn->query($query);
        if ($result->num_rows > 0) {
            // El usuario ya está registrado
            $response["success"] = false;
            $response["message"] = "Username already exists";
        } else {
            // Insertar el nuevo usuario en la base de datos
            $query = "INSERT INTO usuarios (email, username, password) VALUES ('$email', '$username', '$password')";
            if ($conn->query($query) === TRUE) {
                // Registro exitoso
                $response["success"] = true;
                $response["message"] = "Registration successful";
            } else {
                // Error en el registro
                $response["success"] = false;
                $response["message"] = "Registration failed: " . $conn->error; // Mostrar el mensaje de error de MySQL
            }
        }
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
