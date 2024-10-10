module search_engine.search_engine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens search_engine.search_engine to javafx.fxml;
    exports search_engine.search_engine;
}