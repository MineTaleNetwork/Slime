package cc.minetale.slime.loadout;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum DefaultLoadout {
    LOBBY(Loadout.builder().id("lobby")..build());

    private final Loadout loadout;
}
