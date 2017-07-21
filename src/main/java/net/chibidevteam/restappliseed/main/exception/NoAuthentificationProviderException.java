package net.chibidevteam.restappliseed.main.exception;

public class NoAuthentificationProviderException extends Exception {

    private static final long serialVersionUID = 9026274683227596588L;

    public NoAuthentificationProviderException(String msg, Throwable e) {
        super(msg, e);
    }

    public NoAuthentificationProviderException(String msg) {
        super(msg);
    }

}
