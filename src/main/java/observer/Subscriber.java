package observer;

import java.util.List;

public interface Subscriber {
    void update(Notification notification);
    String getName();
    String getEmail();
    boolean isSubscribed();
    List<Notification> getReceivedNotifications();
    void setSubscribed(boolean subscribed);
}