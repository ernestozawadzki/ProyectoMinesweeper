module minesweeper.proyectominesweeper_ernestoz {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens minesweeper.proyectominesweeper_ernestoz to javafx.fxml;
    exports minesweeper.proyectominesweeper_ernestoz;
}