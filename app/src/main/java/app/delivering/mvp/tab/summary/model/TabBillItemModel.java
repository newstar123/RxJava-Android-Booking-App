package app.delivering.mvp.tab.summary.model;

import java.util.List;
import java.util.Map;

public class TabBillItemModel {
    private String groupFrom[];
    private int groupTo[];
    private String childFrom[];
    private int childTo[];
    private List<? extends Map<String, ?>> groupList;
    private List<SortedBillItem> childList;

    public String[] getGroupFrom() {
        return groupFrom;
    }

    public void setGroupFrom(String[] groupFrom) {
        this.groupFrom = groupFrom;
    }

    public int[] getGroupTo() {
        return groupTo;
    }

    public void setGroupTo(int[] groupTo) {
        this.groupTo = groupTo;
    }

    public String[] getChildFrom() {
        return childFrom;
    }

    public void setChildFrom(String[] childFrom) {
        this.childFrom = childFrom;
    }

    public int[] getChildTo() {
        return childTo;
    }

    public void setChildTo(int[] childTo) {
        this.childTo = childTo;
    }

    public List<? extends Map<String, ?>> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<? extends Map<String, ?>> groupList) {
        this.groupList = groupList;
    }

    public List<SortedBillItem> getChildList() {
        return childList;
    }

    public void setChildList(List<SortedBillItem> childList) {
        this.childList = childList;
    }
}
