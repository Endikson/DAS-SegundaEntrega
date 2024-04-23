<?php
// Obtener los datos recibidos en el cuerpo de la solicitud POST
$inputData = file_get_contents('php://input');

// Log de errores para verificar la entrada recibida
error_log("Entrada recibida: " . $inputData);

// Directorio donde se guardarán las imágenes en el servidor
$uploadDirectory = '/imagenes/';

// Verificar si se recibieron datos
if (!empty($inputData)) {
    // Genera un nombre único para la imagen
    $imageName = uniqid('image_') . '.png';

    // Ruta completa de la imagen
    $destination = $_SERVER['DOCUMENT_ROOT'] . $uploadDirectory . $imageName;

    // Guardar la imagen en el servidor
    if (file_put_contents($destination, $inputData)) {
        // La imagen se guardó correctamente
        error_log("Imagen guardada correctamente: " . $destination);
        echo json_encode(array("success" => true, "message" => "Imagen guardada correctamente"));
    } else {
        // Error al guardar la imagen
        $errorMessage = "Error al guardar la imagen en el servidor";
        error_log($errorMessage);
        echo json_encode(array("success" => false, "message" => $errorMessage));
    }
} else {
    // No se recibieron datos
    $errorMessage = "No se recibieron datos de imagen en la solicitud";
    error_log($errorMessage);
    echo json_encode(array("success" => false, "message" => $errorMessage));
}
?>
