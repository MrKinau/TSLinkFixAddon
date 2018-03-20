package de.lpmitkev.kinau;

import de.lpmitkev.kinau.utils.NewNotifyTextMessage;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.teamspeak.backend.Teamspeak;
import net.labymod.teamspeak.backend.listener.notify.NotifyTextMessage;

import java.lang.reflect.Field;
import java.util.List;

public class TSLinkFix extends LabyModAddon {

    @Override
    public void onEnable() {
        try {
            Field listenersField = Teamspeak.class.getDeclaredField("listeners");
            listenersField.setAccessible(true);
            List listeners = (List) listenersField.get(null);
            NotifyTextMessage ntm = null;
            for (Object listener : listeners) {
                if (listener instanceof NotifyTextMessage) {
                    ntm = (NotifyTextMessage) listener;
                }
            }
            if (ntm != null) {
                int index = listeners.indexOf(ntm);
                listeners.set(index, new NewNotifyTextMessage());
            }
            listenersField.set(null, listeners);
            listenersField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
    }

    @Override
    public void loadConfig() {
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
    }

    @Override
    public void saveConfig() {
    }
}
