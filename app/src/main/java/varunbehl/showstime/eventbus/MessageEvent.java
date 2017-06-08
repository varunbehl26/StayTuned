package varunbehl.showstime.eventbus;

/**
 * Created by varunbehl on 07/03/17.
 */

public class MessageEvent {
    private int request;

    public MessageEvent(int request) {
        this.request = request;
    }

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }
}
