package minesweeper.proyectominesweeper_ernestoz;

import javafx.application.Application;      //metodos importados de otras librerias
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * El programa abre una aplicacion basada
 * en el juego de Minesweeper
 *
 * @author Ernesto Zawadzki Hernandez
 * @version 1.0
 * @since 18-4-2023
 */
public class Main extends Application {

    Button easyMode, hardMode;
    Boolean hard = false;
    List<String> fields = new ArrayList<String>();
    List<Integer> mines = new ArrayList<Integer>();
    List<String> suggest = new ArrayList<String>();
    int cantMinas = 5;
    int minasEncontradas = 0;
    int minasCerca;
    String time;
    boolean ladoIzq, ladoDer, esquina, centro;
    boolean gameOver = false;
    Label clockUI = new Label("Tiempo: ");

    /**
     * launches the application
     * @param args Unused
     */
    public static void main(String[] args) { launch(args); }

    /**
     * implementa el codigo para la creacion de la ventana
     * y sus escenas e interfaces, ademas de la logica del juego
     * @param window Ventana para observar la aplicacion
     * @throws Exception
     */
    @Override
    public void start(Stage window) throws Exception {

        window.setTitle("Minesweeper"); //pone titulo a la ventana
        window.setResizable(false);     //inidica tamano fijo a la ventana

        GridPane choices = new GridPane();                     //crea el tipo de interfaz para acomodar los elementos
        choices.setAlignment(Pos.CENTER);                      //indica alineacion de la interfaz
        choices.setHgap(10);                                   //indica distancia horizontal entre elementos
        choices.setVgap(10);                                   //indica distancia vertical entre elementos
        Scene opciones = new Scene(choices, 500, 500);         //crea escena para elegir dificultad

        easyMode = new Button("Normal");    //agrega boton para el modo dummy
        choices.add(easyMode, 1, 1);        //agrega el boton a la interfaz
        hardMode = new Button("Avanzado");  //agrega boton para el boton avanzado
        choices.add(hardMode, 2, 1);        //agrega el boton a la interfaz

        window.setScene(opciones);  //pone la escena de la ventana

        GridPane layout = new GridPane();          //crea el tipo de interfaz para acomodar elementos
        layout.setAlignment(Pos.CENTER);           //indica alineacion de la interfaz
        layout.setVgap(5);                         //indica distancia vertical entre elementos
        layout.setHgap(5);                         //indica distancia horizontal entre elementos
        Scene game = new Scene(layout, 500, 500);  //crea la escena del juego


        Thread clock = new Thread(new Runnable() {              //abilita un reloj que actua como si fuera multithreaded
            /**
             * metodo para correr varios threads de codigo
             * de manera simultanea
             * @exception InterruptedException cuando hay una interrupcion
             */
            @Override
            public void run() {

                int sec = 0;                                    //variable de segundos
                int min = 0;                                    //variable de minutos
                while ((gameOver == false) && (min < 60)) {     //loop para cuando el juego no ha acabado y no ha pasado una hora

                    if (sec > 59) {                             //convierte segundos a minutos
                        sec = 0;                                //regresa los segundos a 0
                        min++;                                  //incrementa los minutos por 1
                    }

                    time = min + ":" + sec;                     //variable que combina minutos y segundos
                    sec++;                                      //incrementa los segundos por 1

                    try { Thread.sleep(1000);                   //espera 1 segundo antes de continuar con el codigo
                    } catch (InterruptedException e) {          //detecta si se da un error
                        throw new RuntimeException(e);          //crea mensaje de error
                    }

                    Platform.runLater(new Runnable() {      //corre el codigo al terminar otra tarea (por alguna razon Javafx parece ser single-threaded)
                        /**
                         * metodo para correr varios threads de codigo
                         * de manera simultanea
                         */
                        @Override
                        public void run() { clockUI.setText("Tiempo: " + time); }   //actualiza el tiempo del reloj
                    });
                }
            }
        });


        easyMode.setOnAction(new EventHandler<ActionEvent>() {
            /**
             * el metodo reconoce cuando el boton es
             * activado e inicia el juego en el modo dummy
             * @param actionEvent Evento que sucede al dar click
             */
            @Override
            public void handle(ActionEvent actionEvent) {
                window.setScene(game);      //cambia a la escena del juego
                clock.start();              //inicia el reloj
            }
        });

        hardMode.setOnAction(new EventHandler<ActionEvent>() {
            /**
             * el metodom reconoce cuando el boton es
             * activado e inicia el juego en el modo avanzado
             * @param actionEvent Evento que sucede al dar click
             */
            @Override
            public void handle(ActionEvent actionEvent) {
                window.setScene(game);      //cambia a la escena del juego
                hard = true;                //indica el modo seleccionado
                clock.start();              //inicia el reloj
            }
        });


        layout.add(clockUI, 9, 2);                                                             //agrega el reloj a la interfaz
        Label minasEncontradasUI = new Label("Minas: " + Integer.toString(minasEncontradas));  //crea label para contar las minas encontradas
        layout.add(minasEncontradasUI, 9, 4);                                                  //agrega el contador a la interfaz
        Label sugerenciasUI = new Label("Sugerencias: " + suggest);                            //crea label para la pila de celdas sugeridas
        layout.add(sugerenciasUI, 9, 6);                                                       //agrega la pila a la interfaz


        for (int i = 1; i < 9; i++) {           //loop para crear las filas de la matriz
            for (int j = 1; j < 9; j++) {       //loop para crear las columnas de la matriz

                fields.add(Integer.toString(i) + "," + Integer.toString(j));                        //agrega un par de coordenadas a la lista de celdas
                Button newField = new Button(Integer.toString(i) + "," + Integer.toString(j));      //crea un boton que indica sus coordenadas
                newField.setId(Integer.toString(i) + "," + Integer.toString(j));                    //asigna identificacion al boton
                layout.add(newField, j, i);                                                         //agrega el boton a las coordenadas correspondientes

                newField.setOnMouseClicked(actionEvent -> {     //detecta cuando se toca un boton newField

                    if (gameOver == false) {                    //detecta si el juego ha acabado

                        if (actionEvent.getButton() == MouseButton.PRIMARY) {       //detecta el left click

                            newField.setText(Integer.toString(mines.get(fields.indexOf(newField.getText()))));  //cambia el texto del boton al valor de la celda
                            newField.setDisable(true);      //desabilita el boton jugado

                            if (Objects.equals(newField.getText(), "-1")) {     //detecta si se escogio una mina

                                gameOver = true;       //determina que el juego termino

                            } else {

                                Random dummy = new Random();           //crea un randomizador
                                String dummyPlay = (Integer.toString(dummy.nextInt(8 - 1 + 1) + 1) + "," + Integer.toString(dummy.nextInt(8 - 1 + 1) + 1));  //asigna la celda a jugar aleatoriamente

                                try { Thread.sleep(2000);            //espera 2 segundos antes de continuar el codigo
                                } catch (InterruptedException e) {   //detecta si se da un error
                                    throw new RuntimeException(e);   //crea mensaje de error
                                }

                                layout.getChildren().remove(layout.lookup("#" + dummyPlay));                                  //busca la identificacion del boton escogido aleatoriamente y lo borra
                                Button COMselected = new Button(Integer.toString(mines.get(fields.indexOf(dummyPlay))));      //crea nuevo boton con el valor de la celda
                                COMselected.setId(dummyPlay);                                                                 //asigna identificacion al nuevo boton
                                layout.add(COMselected, Integer.parseInt(dummyPlay.substring(2,3)), Integer.parseInt(dummyPlay.substring(0,1)));    //agrega el boton a su respectivo campo
                                COMselected.setDisable(true);

                                if (mines.get(fields.indexOf(dummyPlay)) == -1) { gameOver = true; }       //detecta si la jugada aleatoria fue una mina y acaba el juego
                            }
                        }

                        if (actionEvent.getButton() == MouseButton.SECONDARY) {                                 //detecta right click

                            newField.setText(Integer.toString(mines.get(fields.indexOf(newField.getText()))));  //cambia el texto de boton al valor de la celda
                            newField.setDisable(true);                                                          //desactiva el boton
                            minasEncontradas++;                                                                 //incrementa el contador de minas por 1
                            minasEncontradasUI.setText("Minas: " + minasEncontradas);                           //actualiza el contador en la interfaz
                        }

                    }
                });
            }
        }


        for (int i = 0; i < 64; i++) { mines.add(0);}    //crea la lista de las 64 celdas con valores 0
        while (cantMinas > 0) {                          //loop mientras que se asignan las minas
            Random placeMine = new Random();             //crea un nuevo randomizador
            int minePos = placeMine.nextInt(64);         //escoge una posicion de la lista aleatoriamente
            mines.set(minePos, -1);                      //cambia el elemento en dicha posicion por una mina
            cantMinas--;                                 //disminuye la cantidad de minas a colocar
        }


        for (int i = 0; i < 64; i++) {      //loop para revisar toda la lista mines

            ladoIzq = false;                //boolean que representa columna izquierda
            ladoDer = false;                //boolean que representa columna derecha
            esquina = false;                //boolean que representa las esquinas
            centro = false;                 //boolean que representa el resto de las celdas

            if ((i == 0) || (i == 7) || (i == 8) || (i == 63)) {                                //verifica si la celda esta en una esquina
                esquina = true;                                                                 //indica que la celda esta en la esquina
            }
            if ((i == 8) || (i == 16) || (i == 24) || (i == 32) || (i == 40) || (i == 48)) {    //verifica si la celda esta en la columna izquierda
                ladoIzq = true;                                                                 //indica que la celda esta en la columna izquierda
            }
            if ((i == 15) || (i == 23) || (i == 31) || (i == 39) || (i == 47) || (i == 55)) {   //verifica si la celda esta en la columna derecha
                ladoDer = true;                                                                 //indica que la celda esta en la columna derecha
            }
            if (ladoDer == false && ladoIzq == false && esquina == false) { centro = true; }    //indica que la celda debe estar en el centro

            minasCerca = 0;                             //reinicia el contador de minas cercas a la celda
            if (mines.get(i) != -1) {                   //verifica que no se revise una mina

                if (i == 0) {                           //verifica la esquina del noroeste
                    if (mines.get(i + 1) == -1) {       //verifica si hay mina a su este
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i + 8) == -1) {       //verifica si hay mina a su sur
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i + 9) == -1) {       //verifica si hay mina a su sureste
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                }

                if (i == 7) {                           //verifica la esquina al noreste
                    if (mines.get(i - 1) == -1) {       //verifica si hay mina a su oeste
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i + 7) == -1) {       //verifica si hay mina a su sureste
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i + 8) == -1) {       //verifica si hay mina a su sur
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                }

                if (i == 56) {                          //verifica la esquina suroeste
                    if (mines.get(i + 1) == -1) {       //verifica si hay mina a su este
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i - 7) == -1) {       //verifica si hay mina a su noreste
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i - 8) == -1) {       //verifica si hay mina a su norte
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                }

                if (i == 63) {                          //verifica la esquina sureste
                    if (mines.get(i - 1) == -1) {       //verifica si hay mina a su oeste
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i - 8) == -1) {       //verifica si hay mina a su norte
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                    if (mines.get(i - 9) == -1) {       //verifica si hay mina a su noroeste
                        minasCerca = minasCerca + 1;    //agrega una mina al contador
                    }
                }

                if ((i + 1 < 64) && (mines.get(i + 1) == -1) && (centro || ladoIzq)) {              //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }
                if ((i - 1 >= 0) && (mines.get(i - 1) == -1) && (centro || ladoDer)) {              //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }
                if ((i + 7 < 64) && (mines.get(i + 7) == -1) && (centro || ladoDer)) {              //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }
                if ((i - 7 >= 0) && (mines.get(i - 7) == -1) && (centro || ladoIzq)) {              //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }
                if ((i + 8 < 64) && (mines.get(i + 8) == -1) && (centro || ladoIzq || ladoDer)) {   //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }
                if ((i - 8 >= 0) && (mines.get(i - 8) == -1) && (centro || ladoIzq || ladoDer)) {   //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }
                if ((i + 9 < 64) && (mines.get(i + 9) == -1) && (centro || ladoIzq)) {              //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }
                if ((i - 9 >= 0) && (mines.get(i - 9) == -1) && (centro || ladoDer)) {              //verifica que haya una mina dentro del rango correcto
                    minasCerca = minasCerca + 1;                                                    //agrega una mina al contador
                }

                mines.set(i, minasCerca);       //actualiza el valor del elemento
            }
        }


        window.show();      //muestra la ventana
    }
}