<?php
// Ruta del directorio donde se almacenan las imágenes
$directory = 'imagenes/';

// Obtener una lista de nombres de archivo de imágenes en el directorio
$files = glob($directory . '*.{jpg,jpeg,png,gif}', GLOB_BRACE);

// Crear un array para almacenar las URLs de las imágenes
$imageUrls = array();

// Iterar sobre los archivos y agregar las URL al array
foreach ($files as $file) {
    $imageUrl = 'http://' . $_SERVER['HTTP_HOST'] . '/' . $file;
    array_push($imageUrls, $imageUrl);
}

// Devolver las URLs de las imágenes como respuesta
echo json_encode($imageUrls);
?>
