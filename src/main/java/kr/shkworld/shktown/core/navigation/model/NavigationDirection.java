package kr.shkworld.shktown.core.navigation.model;

import kr.shkworld.shktown.core.common.model.Position;

public enum NavigationDirection {
    UP("위로 이동 ⬆"),
    DOWN("아래로 이동 ⬇"),
    FORWARD("앞으로 이동 ⬆"),
    RIGHT("오른쪽으로 이동 ➡"),
    BACKWARD("뒤로 이동 ⬇"),
    LEFT("왼쪽으로 이동 ⬅");

    private final String text;

    NavigationDirection(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static NavigationDirection calculate(Position from, Position to, double heightThreshold) {
        double yDiff = to.y() - from.y();

        if (yDiff >= heightThreshold) {
            return UP;
        } else if (yDiff <= -heightThreshold) {
            return DOWN;
        }

        double dx = to.x() - from.x();
        double dz = to.z() - from.z();

        double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));

        double relativeAngle = (targetYaw - from.yaw()) % 360;
        if (relativeAngle < -180) relativeAngle += 360;
        if (relativeAngle > 180) relativeAngle -= 360;

        if (relativeAngle >= -45 && relativeAngle < 45) {
            return FORWARD;
        } else if (relativeAngle >= 45 && relativeAngle < 135) {
            return RIGHT;
        } else if (relativeAngle >= -135 && relativeAngle < -45) {
            return LEFT;
        } else {
            return BACKWARD;
        }
    }
}
