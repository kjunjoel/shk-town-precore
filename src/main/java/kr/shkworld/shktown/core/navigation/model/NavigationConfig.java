package kr.shkworld.shktown.core.navigation.model;

public record NavigationConfig(
        String title,
        String betterhudPointerId,
        double arrivalRadius,
        int updateIntervalTicks,

        boolean useGlobalPrefix,
        String started,
        String stopped,
        String arrived,
        String differentWorld
) {}
