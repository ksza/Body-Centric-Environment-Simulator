package dk.itu.bodysim.notifications;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import dk.itu.bodysim.EgocentricApp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationsStateManager extends AbstractAppState {

    /**
     * Each notification lives for 5 secods
     */
    private static final int NOTIFICATION_TIMEOUT = 5;
    
    private Node rootNode;
    private EgocentricApp app;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private List<Notification> notifications = new ArrayList<Notification>();
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private BitmapText notificationsPanel;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (EgocentricApp) app;
        this.stateManager = stateManager;
        this.assetManager = app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        
        initNotifications();
    }

    private String getSerialisedNotifications() {
        final StringBuilder sb = new StringBuilder();

        for (final Notification notif : notifications) {
            sb.append(notif.getMessage()).append("\n");
        }

        return sb.toString();
    }

    public synchronized void addNotification(final String notificationMessage) {

        final Notification notification = new Notification(notificationMessage);
        notifications.add(notification);
        displayNotifications();

        Runnable task = new Runnable() {
            public void run() {
                removeNotification(notification);
            }
        };
        worker.schedule(task, NOTIFICATION_TIMEOUT, TimeUnit.SECONDS);
    }

    private synchronized void removeNotification(final Notification notification) {
        notifications.remove(notification);
        displayNotifications();
    }

    private void initNotifications() {
        app.setDisplayStatView(false);
        final BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        notificationsPanel = new BitmapText(font, false);
        notificationsPanel.setSize(font.getCharSet().getRenderedSize());
        notificationsPanel.setText(getSerialisedNotifications());
        notificationsPanel.setLocalTranslation(3, app.getSettings().getHeight() - 3, 1);
        app.getGuiNode().attachChild(notificationsPanel);
    }

    private synchronized void displayNotifications() {
        notificationsPanel.setText(getSerialisedNotifications());
    }
}