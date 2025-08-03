package org.ventura.soluciones.sunatws_ose.tci.consumer;

public final class Consumer {

    private String username;
    private String password;

    public Consumer() {
    }

    public Consumer(String username, String password) {
        this.username = username;
        this.password = password;
    } //Consumer


    public String getUsername() {
        return username;
    } //getUsername

    public void setUsername(String username) {
        this.username = username;
    } //setUsername

    public String getPassword() {
        return password;
    } //getPassword

    public void setPassword(String password) {
        this.password = password;
    } //setPassword

} //Consumer
