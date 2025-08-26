package de.pitbully.pitbullyplugin.listeners;

import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.utils.TpaRequestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class PlayerQuitCleanupListenerTest {

    @Test
    void clearsRequestsOnQuit() {
        Player p = mock(Player.class);
        PlayerQuitEvent evt = mock(PlayerQuitEvent.class);
        when(evt.getPlayer()).thenReturn(p);

        try (MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class)) {
            new PlayerQuitCleanupListener().onQuit(evt);
            mgr.verify(() -> TpaRequestManager.clearFor(p));
        }
    }
}
