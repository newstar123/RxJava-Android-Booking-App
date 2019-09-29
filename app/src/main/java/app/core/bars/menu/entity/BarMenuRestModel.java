package app.core.bars.menu.entity;

import java.util.List;

public class BarMenuRestModel {
    private boolean success;
    private List<BarMenuModel> menu;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<BarMenuModel> getMenu() {
        return menu;
    }

    public void setMenu(List<BarMenuModel> menu) {
        this.menu = menu;
    }
}
