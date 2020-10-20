import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {



    public static void main(String[] args) throws IOException {
        motrarMenu();
    }

    private static void motrarMenu() throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce un numero en función de lo que quieras hacer:"+"\n"+
                "1. Para crear carpeta"+"\n"+
                "2. Crea un fichero"+"\n"+"3. Lista todas las interfaces"+"\n"+
                "4. Muestra la IP"+"\n"+
                "5. Muestra la MAC"+"\n"+
                "6. Comprueba conectividad"+"\n"+
                "7. Salir"+"\n");

        int numero=scanner.nextInt();

        switch (numero){
            case 1:
                crearcarpeta();
                break;
            case 2:
                crearfichero();
                break;
            case 3:
                listarInterfaces();
                break;
            case 4:
                mostrarIp();
                break;
            case 5:
                mostrarMac();
                break;
            case 6:
                comprobarConexion();
                break;
            case 7:
                System.out.println("Adiós,gracias por su visita");
                break;
            default:
                System.out.println("Solo numeros entre 1 y 7");
        }
    }



    /*
    Método auxiliar check ruta para verificar que la ruta existe
    */
    private static boolean checkRuta(String ruta) {
        return new File(ruta).exists();
    }



    /*
    Método auxiliar check Nombre Archivo para verificar si la carpeta existe
    */
    private static boolean checkNombreArchivo(String nombrecarpeta) {
        return new File(nombrecarpeta).exists();
    }


    /*
    Método auxiliar check Nombre Fichero para verificar si el Fichero existe
    */
    private static boolean checkNombreFichero(String nombrefichero) {
        return new File(nombrefichero).exists();
    }




    /*
    Metodo crearcarpeta en el que pediremos la ruta en la que queremos crearlo y el nombre
    después comprobaremos que exista la ruta y no exista la carpeta y lo ejecutaremos.
    */
    private static void crearcarpeta() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Dime la ruta de la carpeta");
        String ruta = scanner.nextLine();
        System.out.println("Dime el nombre de la carpeta");
        String nombrecarpeta = scanner.nextLine();
        String comando = "mkdir "+ruta+"\\"+nombrecarpeta;

        if (!checkRuta(ruta)) {
            System.out.println("La ruta no existe");
        }else if(checkNombreArchivo(ruta+"\\"+nombrecarpeta)){
            System.out.println("Este fichero ya existe");
        }
        else {
            processBuilder.command("cmd.exe", "/c", comando);
            processBuilder.start();
            System.out.println("La carpeta con nombre "+nombrecarpeta+" ha sido creada"+"\n");
        }
        preguntaSalir();
    }




    /*
    Metodo crearfichero en el que pediremos la ruta en la que queremos crearlo y el nombre
    después comprobaremos que exista la ruta y no exista el fichero y lo ejecutaremos.
    */
    private static void crearfichero() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Dime la ruta del fichero");
        String ruta = scanner.nextLine();
        System.out.println("Dime el nombre del fichero");
        String nombrefichero = scanner.nextLine();
        String comando = "type NUL > "+ruta+"\\"+nombrefichero;


        if (!checkRuta(ruta)) {
            System.out.println("La ruta no existe"+"\n");
        }else if(checkNombreFichero(ruta+"\\"+nombrefichero)){
            System.out.println("El fichero ya existe");
        }
        else {
            processBuilder.command("cmd.exe", "/c", comando);
            processBuilder.start();
            System.out.println("El fichero con nombre "+nombrefichero+" ha sido creado"+"\n");
        }

        preguntaSalir();
    }





    /*
    listarInterfaces es un método para listar todas las interfaces de red, lo que hacemos es
    ejecutar el proceso y después leer con un bf reader lo que ha salido en el cmd
    */
    private static void listarInterfaces() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe","/c","ipconfig");

        try {

            Process process = processBuilder.start();

            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //lectura de lo que ha pasado en cmd
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (process.waitFor() == 0) {
                System.out.println(buffer+"\n");
            } else {
                System.out.println("Error"+"\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preguntaSalir();

    }





    /*
    En el método mostrarIp mostraremos la ip de una interfaz de red
    Para ello ejecutaremos el proceso en el powershell buscando la interfaz y diciendo que imprima
    la linea donde está la IP, si el nombre de la interfaz es incorrecto volveremos a llamar al mismo método para
    introducir bien el nombre de la interfaz, cuando lo introducimos bien nos aparece en pantalla.
     */

    private static void mostrarIp() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();


        Scanner scanner = new Scanner(System.in);
        System.out.println("Dime la interfaz de la que quieres la IP:");
        String interfaz= scanner.nextLine();
        processBuilder.command("powershell.exe","/c","Get-NetAdapter -Name "+ interfaz+" | Get-NetIpAddress -AddressFamily IPv4");

        try {

            Process process = processBuilder.start();
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //lectura de lo que ha pasado en cmd
            String line;


            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (process.waitFor() == 0) {
                Scanner sc=new Scanner(buffer.toString());
                while (sc.hasNextLine()){
                    String lineaIP = sc.nextLine();
                    if((lineaIP.contains("IPAddress"))){
                        System.out.println(lineaIP+"\n");
                        break;
                    }
                }
            } else {
                System.out.println("error, ese adaptador no existe, prueba otra vez"+"\n");
                mostrarIp();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preguntaSalir();
    }





    /*
    En el método mostrarMac mostraremos la Mac de una interfaz de red
    Para ello ejecutaremos el proceso en el powershell buscando la interfaz y diciendo que imprima
    la Mac de esta interfaz, si el nombre de la interfaz es incorrecto volveremos a llamar al mismo método para
    introducir bien el nombre de la interfaz, cuando lo introducimos bien nos aparece en pantalla. Nos vamos a ayudar de
    un boolean el cual lo declaramos como verdadero y si podemos encontrar la MAC cambiamos a falso.
    */

    private static void mostrarMac() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        boolean error=true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Dime la interfaz de la que quieres la MAC:");
        String interfaz= scanner.nextLine();
        processBuilder.command("powershell.exe","/c","(Get-NetAdapter -Name "+ interfaz+").MacAddress");

        try {

            Process process = processBuilder.start();

            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //lectura de lo que ha pasado en cmd
            String line;


            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }


            if (process.waitFor() == 0) {
                Scanner sc=new Scanner(buffer.toString());
                while (sc.hasNextLine()){
                    String lineaMAC = sc.nextLine();
                    if((lineaMAC.contains("-"))){
                        System.out.println("MAC: "+lineaMAC+"\n");
                        error=false;
                        break;
                    }
                }
            }


            if(error==true){
                System.out.println("error, ese adaptador no existe, prueba otra vez"+"\n");
                mostrarMac();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        preguntaSalir();
    }




    /*
    En el método comprobar conexión realizamos un proceso en el que intenta conectar con una página web
    silo logra, imprimimos que da conexión si no, nos da error. Nos vamos a ayudar de un boolean el cual
    lo declaramos como verdadero y si podemos conectarnos lo cambiamos a falso.
     */

    private static void comprobarConexion() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("powershell.exe","/c","Test-NetConnection -ComputerName www.google.es");

        boolean error=true;

        try {

            Process process = processBuilder.start();

            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //lectura de lo que ha pasado en cmd
            String line;


            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (process.waitFor() == 0) {
                Scanner sc=new Scanner(buffer.toString());
                while (sc.hasNextLine()){
                    String lineaIP = sc.nextLine();
                    if((lineaIP.contains("True"))){
                        System.out.println("Hay conexion"+"\n");
                        error=false;
                    }
                }

            }

            if(error){
                System.out.println("error, no hay intenet"+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        preguntaSalir();
    }



    /*
    Este método auxiliar lo introduzco al final de cada uno de los métodos principales por si el usuario quiere
    realizar otra opción
     */

    private static void preguntaSalir() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce un numero en función de lo que quieras hacer:"+"\n"+
                "1. Volver al menu principal"+"\n"+
                "2. Salir");

        int numero=scanner.nextInt();

        if(numero==1){
            motrarMenu();
        }else if(numero==2){
            System.out.println("ADIOS"+"\n");
        }else{
            System.out.println("Solo un número entre el 1 y el 2"+"\n");
            preguntaSalir();
        }
    }

}






