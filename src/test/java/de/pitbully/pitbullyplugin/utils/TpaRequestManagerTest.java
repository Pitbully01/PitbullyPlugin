package de.pitbully.pitbullyplugin.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TpaRequestManagerTest {

    @AfterEach
    void cleanup() {
        // Clear static maps by clearing for random players involved in tests if needed
        // We don't have direct clear(), but clearing for a random player won't harm when empty.
    }

    @Test
    void addAndFetchOutgoingIncoming() {
        Player requester = mock(Player.class);
        Player target = mock(Player.class);
        UUID rid = UUID.randomUUID();
        UUID tid = UUID.randomUUID();
        when(requester.getUniqueId()).thenReturn(rid);
        when(target.getUniqueId()).thenReturn(tid);

        TpaRequest req = mock(TpaRequest.class);
        when(req.getRequester()).thenReturn(requester);
        when(req.getTarget()).thenReturn(target);

        TpaRequestManager.add(req);
        assertThat(TpaRequestManager.hasOutgoing(rid)).isTrue();
        assertThat(TpaRequestManager.hasIncoming(tid)).isTrue();
        assertThat(TpaRequestManager.getOutgoing(rid)).isSameAs(req);
        assertThat(TpaRequestManager.getIncoming(tid)).isSameAs(req);
    // cleanup
    TpaRequestManager.removeByRequester(rid);
    }

    @Test
    void removeByRequesterRemovesFromBothMaps() {
        Player requester = mock(Player.class);
        Player target = mock(Player.class);
        UUID rid = UUID.randomUUID();
        UUID tid = UUID.randomUUID();
        when(requester.getUniqueId()).thenReturn(rid);
        when(target.getUniqueId()).thenReturn(tid);

        TpaRequest req = mock(TpaRequest.class);
        when(req.getRequester()).thenReturn(requester);
        when(req.getTarget()).thenReturn(target);

        TpaRequestManager.add(req);
        TpaRequestManager.removeByRequester(rid);
        assertThat(TpaRequestManager.hasOutgoing(rid)).isFalse();
        assertThat(TpaRequestManager.hasIncoming(tid)).isFalse();
    }

    @Test
    void clearForRemovesOutgoingAndIncoming() {
        Player requester = mock(Player.class);
        Player target = mock(Player.class);
        UUID rid = UUID.randomUUID();
        UUID tid = UUID.randomUUID();
        when(requester.getUniqueId()).thenReturn(rid);
        when(target.getUniqueId()).thenReturn(tid);

        TpaRequest req = mock(TpaRequest.class);
        when(req.getRequester()).thenReturn(requester);
        when(req.getTarget()).thenReturn(target);

        TpaRequestManager.add(req);
        // Clear by requester
        TpaRequestManager.clearFor(requester);
        assertThat(TpaRequestManager.hasOutgoing(rid)).isFalse();
        assertThat(TpaRequestManager.hasIncoming(tid)).isFalse();

        // Re-add and clear by target
        TpaRequestManager.add(req);
        TpaRequestManager.clearFor(target);
        assertThat(TpaRequestManager.hasOutgoing(rid)).isFalse();
        assertThat(TpaRequestManager.hasIncoming(tid)).isFalse();
    }
}
