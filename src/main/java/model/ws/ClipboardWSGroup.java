package model.ws;

public class ClipboardWSGroup extends WSGroup {

    static class Holder{
        static final ClipboardWSGroup inst = new ClipboardWSGroup();
    }

    public static WSGroup inst() {
        return Holder.inst;
    }

}
