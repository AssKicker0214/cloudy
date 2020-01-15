package model.ws;

import utils.JsonUtil;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ControlMessage implements Serializable {
    private String cmd;
    private List<String> args;

    public ControlMessage(String cmd, String... args) {
        this.cmd = cmd;
        this.args = Arrays.asList(args);
    }

    public String getCmd() {
        return this.cmd;
    }

    public List<String> getArgs() {
        return this.args;
    }
}
