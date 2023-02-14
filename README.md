# AndroidRemote
Aplicación de control remoto para Android Cliente/Servidor.
-Conección TCP Reversa en segundo plano  
-Servidor para múltiples clientes  


El servidor compilarlo en NetBeans  y el cliente en Android Studio



	# FUNCIONES #

-Info del dispositivo y geolocalización  
-Ver y enviar SMS  
-Ver y descargar archivos  
-Ver todas las aplicaciones instaladas  
-Generación y descarga de miniaturas de imágenes  
-Generación y descarga de "captura miniatura" de videos  

![alt text](https://i.ibb.co/FhwQcP7/img1.png)

![alt text](https://i.ibb.co/BsFmb6b/img2.png)


	# EJEMPLO CON NGROK #
-Abrir conección tcp con ngrok en el puerto deseado, por ejemplo "ngrok tcp 1234"  

-En la app de android en Config usar el host y puerto que devuelve el paso anterior  

-Ejecutar cliente en tu Android, va a tratar de conectarse siempre que no esté conectado. Se puede cerrar la app, queda ejecutando en segundo plano  

-Para poner server a la escucha ejecutar la aplicación Servidor, indicar en este caso puerto 1234 y click a Start  

-Para detener el servicio click en la notificación generada en tu Android  
