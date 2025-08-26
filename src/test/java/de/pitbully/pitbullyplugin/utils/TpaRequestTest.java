package de.pitbully.pitbullyplugin.utils;

import static org.mockito.Mockito.*;

import de.pitbully.pitbullyplugin.PitbullyPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TpaRequestTest {

    @Test
    void sendRequestRejectsWhenDuplicateOutgoingOrIncoming() {
        Player requester = mock(Player.class);
        Player target = mock(Player.class);
        when(requester.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        when(target.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        when(target.getName()).thenReturn("Bob");

        // Avoid scheduling by forcing hasOutgoing true
        try (MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class)) {
            mgr.when(() -> TpaRequestManager.hasOutgoing(any())).thenReturn(true);
            new TpaRequest(requester, target).sendRequest();
            verify(requester).sendMessage(contains("bereits eine aktive"));
        }

        // Now force hasIncoming true path
        try (MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class)) {
            mgr.when(() -> TpaRequestManager.hasOutgoing(any())).thenReturn(false);
            mgr.when(() -> TpaRequestManager.hasIncoming(any())).thenReturn(true);
            new TpaRequest(requester, target).sendRequest();
            verify(requester).sendMessage(contains("bereits eine offene"));
        }
    }

    @Test
    void acceptRequestTeleportsRequesterAndCleansUp() {
        Player requester = mock(Player.class);
        Player target = mock(Player.class);
        when(target.isOnline()).thenReturn(true);
        World w = mock(World.class); when(w.getName()).thenReturn("world");
        Location loc = new Location(w, 0, 64, 0);
        when(target.getLocation()).thenReturn(loc);

        try (MockedStatic<SafeTeleport> st = mockStatic(SafeTeleport.class);
             MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class);
             MockedStatic<PitbullyPlugin> plugin = mockStatic(PitbullyPlugin.class)) {
            st.when(() -> SafeTeleport.teleport(eq(requester), eq(loc))).thenReturn(true);
            PitbullyPlugin pl = mock(PitbullyPlugin.class);
            plugin.when(PitbullyPlugin::getInstance).thenReturn(pl);
            // No config manager needed for accept branch

            new TpaRequest(requester, target).acceptRequest();
            verify(requester).sendMessage(contains("teleportiert"));
            // Manager removal must be called on accept
            mgr.verify(() -> TpaRequestManager.removeByRequester(requester.getUniqueId()));
        }
    }

    @Test
    void denyRequestNotifiesAndCleansUp() {
        Player requester = mock(Player.class);
        Player target = mock(Player.class);
        when(target.getName()).thenReturn("Bob");

        try (MockedStatic<TpaRequestManager> mgr = mockStatic(TpaRequestManager.class)) {
            new TpaRequest(requester, target).denyRequest();
            verify(requester).sendMessage(contains("abgelehnt"));
            mgr.verify(() -> TpaRequestManager.removeByRequester(requester.getUniqueId()));
        }
    }
}
