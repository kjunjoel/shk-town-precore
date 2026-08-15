package kr.shkworld.shktown.core.navigation.model;

import kr.shkworld.shktown.core.common.model.Position;

import java.util.Comparator;
import java.util.Map;

public enum NavigationSortType {
    NAME_ASC("오름차순"),
    NAME_DESC("내림차순"),
    DISTANCE_ASC("가까운순"),
    DISTANCE_DESC("먼순");

    private final String displayName;

    NavigationSortType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NavigationSortType next() {
        NavigationSortType[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public Comparator<Map.Entry<String, Position>> getComparator(Position playerPosition) {
        return switch (this) {
            case NAME_DESC -> Map.Entry.comparingByKey(Comparator.reverseOrder());
            case DISTANCE_ASC -> Comparator.comparingDouble(entry -> entry.getValue().distanceSquared(playerPosition));
            case DISTANCE_DESC -> Comparator.comparingDouble((Map.Entry<String, Position> entry) ->
                    entry.getValue().distanceSquared(playerPosition)).reversed();
            default -> Map.Entry.comparingByKey();
        };
    }
}
