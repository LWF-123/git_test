package com.lwf.hospital.ui.component;

import com.lwf.hospital.common.exception.GlobalExceptionHandler;
import com.lwf.hospital.common.util.InputUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 控制台菜单：展示选项 → 读取选择 → 执行动作 → 捕获异常后继续。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class Menu {

    private final String title;
    private final Map<Integer, String> labels = new LinkedHashMap<>();
    private final Map<Integer, Runnable> actions = new LinkedHashMap<>();

    public Menu(String title) {
        this.title = title;
    }

    /**
     * 添加菜单项。
     *
     * @param key    序号
     * @param label  文字
     * @param action 动作
     * @return 当前菜单，便于链式添加
     */
    public Menu add(int key, String label, Runnable action) {
        labels.put(key, label);
        actions.put(key, action);
        return this;
    }

    /**
     * 进入菜单循环，选择 0 返回上一级。
     */
    public void run() {
        while (true) {
            render();
            int choice = InputUtils.readInt("  请选择：", 0, maxKey());
            if (choice == 0) {
                return;
            }
            Runnable action = actions.get(choice);
            if (action == null) {
                System.out.println("  无效的选项：" + choice);
                continue;
            }
            try {
                action.run();
            } catch (Exception e) {
                // 局部异常处理：单个功能出错不退出菜单
                GlobalExceptionHandler.handle(e);
            }
        }
    }

    private void render() {
        System.out.println();
        System.out.println("  ┌─ " + title);
        labels.forEach((key, label) -> System.out.println("  │  " + key + ". " + label));
        System.out.println("  └─  0. 返回");
    }

    private int maxKey() {
        return labels.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }
}