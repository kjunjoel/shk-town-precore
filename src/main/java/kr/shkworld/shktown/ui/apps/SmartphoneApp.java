package kr.shkworld.shktown.ui.apps;

import org.bukkit.inventory.ItemStack;

/**
 * 스마트폰 메인 화면에 표시할 앱의 설정값입니다.
 */
public record SmartphoneApp(String id, int order, ItemStack item) {
}
