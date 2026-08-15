package kr.shkworld.shktown.core.taxi.model;

public record TaxiConfig(
        String defaultMap,
        boolean requireTaxiStop,
        double allowedRadius,

        String titleMain,
        String titleSub,
        int titleFadeInMs,
        int titleStayMs,
        int titleFadeOutMs,
        long teleportDelayTicks,

        boolean useGlobalPrefix,
        String notInStopMessage,
        String arrivedMessage,
        String loadingAppMessage
) {}