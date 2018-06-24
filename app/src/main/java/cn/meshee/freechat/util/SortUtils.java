package cn.meshee.freechat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cn.meshee.freechat.app.FreechatContact;

public class SortUtils {

    public static void sortContacts(List<FreechatContact> list) {
        Collections.sort(list);
        List<FreechatContact> specialList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getNameSpelling().equalsIgnoreCase("#")) {
                specialList.add(list.get(i));
            }
        }
        if (specialList.size() != 0) {
            list.removeAll(specialList);
            list.addAll(list.size(), specialList);
        }
    }
}
