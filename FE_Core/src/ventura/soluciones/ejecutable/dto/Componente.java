package ventura.soluciones.ejecutable.dto;

import org.kordamp.ikonli.Ikon;

public class Componente {

    private String key;

    private String name;

    private String path;

    private Ikon icon;

    public Componente(String key, String name, Ikon icon) {
        this.key = key;
        this.name = name;
        this.icon = icon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Ikon getIcon() {
        return icon;
    }

    public void setIcon(Ikon icon) {
        this.icon = icon;
    }
}
